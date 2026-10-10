package com.travelplanner.travel_planner_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int expenseId;
  private double amount;
  @Enumerated(EnumType.STRING)
  private ExpenseType category;
  private String currency;
  private String paymentMethod;
  private LocalDate expenseDate;
  @ManyToOne
  @JoinColumn(name = "trip_id")
  private Trip trip;
}
