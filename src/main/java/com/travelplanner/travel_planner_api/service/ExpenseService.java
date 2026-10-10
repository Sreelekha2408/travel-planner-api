package com.travelplanner.travel_planner_api.service;

import com.travelplanner.travel_planner_api.dto.expenses.ExpenseRequest;
import com.travelplanner.travel_planner_api.entities.Expense;
import com.travelplanner.travel_planner_api.entities.Trip;
import com.travelplanner.travel_planner_api.mapper.ExpenseMapper;
import com.travelplanner.travel_planner_api.repo.ExpenseRepo;
import com.travelplanner.travel_planner_api.repo.TripRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExpenseService {

  @Autowired
  ExpenseRepo expenseRepo;

  @Autowired
  ExpenseMapper expenseMapper;

  @Autowired
  TripRepo tripRepo;

  public Expense createExpense(int tripId, ExpenseRequest expenseRequest) {
    Trip trip = tripRepo.findById(tripId).orElseThrow(() -> new IllegalArgumentException("Trip not found"));
    String username = trip.getUser().getUsername();
    String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
    if(username.equals(loggedInUser)) {
      Expense expense = expenseMapper.toEntity(expenseRequest);
      expense.setTrip(trip);
      return expenseRepo.save(expense);
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Trip doesn't exist for this user");
    }
  }

  public List<Expense> getAllExpenses(int tripId) {
    return expenseRepo.findAllByTrip_TripId(tripId);
  }

  public Expense updateExpense(int expenseId, ExpenseRequest expenseRequest) {
    Expense expense = expenseRepo.findById(expenseId).orElseThrow(() -> new NoSuchElementException("Expense not found"));
    Trip trip = tripRepo.findById(expense.getTrip().getTripId()).orElseThrow(() -> new IllegalArgumentException("Trip not found"));
    String username = trip.getUser().getUsername();
    String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
    if(username.equals(loggedInUser)) {
      expense.setAmount(expenseRequest.getAmount());
      expense.setTrip(trip);
      expense.setExpenseDate(expenseRequest.getExpenseDate());
      expense.setCurrency(expenseRequest.getCurrency());
      expense.setPaymentMethod(expenseRequest.getPaymentMethod());
      expense.setCategory(expenseRequest.getCategory());
      return expenseRepo.save(expense);
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to update this expense");
    }
  }

  public String deleteExpense(int expenseId) {
    Expense expense = expenseRepo.findById(expenseId).orElseThrow(() -> new NoSuchElementException("Expense not found"));
    Trip trip = expense.getTrip();
    String username = trip.getUser().getUsername();
    String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
    if(username.equals(loggedInUser)) {
      expenseRepo.deleteById(expenseId);
      return "Expense deleted with id: " + expenseId;
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to delete this expense");
    }
  }
}
