package com.travelplanner.travel_planner_api.controller;

import com.travelplanner.travel_planner_api.dto.expenses.ExpenseRequest;
import com.travelplanner.travel_planner_api.dto.expenses.ExpenseResponse;
import com.travelplanner.travel_planner_api.entities.Expense;
import com.travelplanner.travel_planner_api.mapper.ExpenseMapper;
import com.travelplanner.travel_planner_api.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

  @Autowired
  ExpenseService expenseService;

  @Autowired
  ExpenseMapper expenseMapper;

  @PostMapping("/{tripId}")
  public ExpenseResponse createExpense(@PathVariable int tripId, @RequestBody ExpenseRequest expenseRequest) {
    Expense expense = expenseService.createExpense(tripId, expenseRequest);
    return expenseMapper.toResponse(expense);
  }

  @GetMapping("/{tripId}")
  public List<ExpenseResponse> getAllExpenses(@PathVariable int tripId) {
    List<Expense> list = expenseService.getAllExpenses(tripId);
    List<ExpenseResponse> response = new ArrayList<>();
    for (Expense expense : list) {
      response.add(expenseMapper.toResponse(expense));
    }
    return response;
  }

  @PutMapping("/{expenseId}")
  public ExpenseResponse updateExpense(@PathVariable int expenseId, @RequestBody ExpenseRequest expenseRequest) {
    Expense expense = expenseService.updateExpense(expenseId, expenseRequest);
    return expenseMapper.toResponse(expense);
  }

  @DeleteMapping("/{expenseId}")
  public String deleteExpense(@PathVariable int expenseId) {
    return expenseService.deleteExpense(expenseId);
  }
}
