package com.travelplanner.travel_planner_api.service;

import com.travelplanner.travel_planner_api.dto.expenses.ExpenseRequest;
import com.travelplanner.travel_planner_api.entities.Expense;
import com.travelplanner.travel_planner_api.entities.Trip;
import com.travelplanner.travel_planner_api.mapper.ExpenseMapper;
import com.travelplanner.travel_planner_api.repo.ExpenseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Every method first checks the trip (or the expense's trip) belongs to the signed-in user.
// Someone else's trip or expense gets the same 404 as a missing one, so ids can't be probed.
@Service
public class ExpenseService {

  @Autowired
  ExpenseRepo expenseRepo;

  @Autowired
  ExpenseMapper expenseMapper;

  @Autowired
  TripService tripService;

  public Expense createExpense(int tripId, ExpenseRequest expenseRequest) {
    Trip trip = tripService.getTripById(tripId); // 404 unless it's yours
    Expense expense = expenseMapper.toEntity(expenseRequest);
    expense.setTrip(trip);
    expense.setCurrency(currencyOrTrips(expenseRequest, trip));
    return expenseRepo.save(expense);
  }

  // Newest first, like the design's "Recent Transactions".
  public List<Expense> getAllExpenses(int tripId) {
    Trip trip = tripService.getTripById(tripId);
    return expenseRepo.findAllByTrip_TripIdOrderByExpenseDateDescExpenseIdDesc(trip.getTripId());
  }

  public Expense updateExpense(int expenseId, ExpenseRequest expenseRequest) {
    Expense expense = findOwnedExpense(expenseId);
    expense.setTitle(expenseRequest.getTitle());
    expense.setAmount(expenseRequest.getAmount());
    expense.setExpenseDate(expenseRequest.getExpenseDate());
    expense.setCurrency(currencyOrTrips(expenseRequest, expense.getTrip()));
    expense.setPaymentMethod(expenseRequest.getPaymentMethod());
    expense.setCategory(expenseRequest.getCategory());
    return expenseRepo.save(expense);
  }

  public String deleteExpense(int expenseId) {
    expenseRepo.delete(findOwnedExpense(expenseId));
    return "Expense deleted with id: " + expenseId;
  }

  private Expense findOwnedExpense(int expenseId) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return expenseRepo.findById(expenseId)
            .filter(expense -> expense.getTrip() != null && expense.getTrip().getUser() != null
                    && expense.getTrip().getUser().getUsername().equals(username))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
  }

  // Expenses sent without a currency use the trip's, so totals add up in one currency.
  private String currencyOrTrips(ExpenseRequest expenseRequest, Trip trip) {
    return expenseRequest.getCurrency() != null ? expenseRequest.getCurrency() : trip.getCurrency();
  }
}
