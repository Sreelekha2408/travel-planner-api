package com.travelplanner.travel_planner_api.dto.expenses;

import com.travelplanner.travel_planner_api.entities.ExpenseType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseResponse {
  private int expenseId;
  private String title;
  private double amount;
  private ExpenseType category;
  private String currency;
  private String paymentMethod;
  private LocalDate expenseDate;
  private int tripId;
}