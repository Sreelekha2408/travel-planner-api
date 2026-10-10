package com.travelplanner.travel_planner_api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {
  private int totalTrips;
  private double totalExpenses;
  private double totalBudget;
  private double remainingBudget;
  private int upcomingTrips;
}
