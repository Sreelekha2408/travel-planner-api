package com.travelplanner.travel_planner_api.dto.expenses;

import com.travelplanner.travel_planner_api.entities.ExpenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequest {

  @NotBlank(message = "Title is required")
  @Size(max = 255, message = "Title must be 255 characters or fewer")
  private String title;

  @Positive(message = "Amount must be more than 0")
  private double amount;

  @NotNull(message = "Category is required")
  private ExpenseType category;

  // ISO 4217 code like the trip's (e.g. INR). Optional: defaults to the trip's currency.
  @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter code like INR")
  private String currency;

  @Size(max = 255, message = "Payment method must be 255 characters or fewer")
  private String paymentMethod;

  @NotNull(message = "Date is required")
  private LocalDate expenseDate;

  private int tripId;
}
