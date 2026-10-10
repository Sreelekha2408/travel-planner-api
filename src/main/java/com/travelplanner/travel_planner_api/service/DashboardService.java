package com.travelplanner.travel_planner_api.service;

import com.travelplanner.travel_planner_api.dto.dashboard.Dashboard;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.repo.ExpenseRepo;
import com.travelplanner.travel_planner_api.repo.TripRepo;
import com.travelplanner.travel_planner_api.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

  @Autowired
  TripRepo tripRepo;

  @Autowired
  UserRepo userRepo;

  @Autowired
  ExpenseRepo expenseRepo;

  public Dashboard getDashboardDetails() {
    Dashboard dashboard = new Dashboard();
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepo.findByUsername(username);
    LocalDate today = LocalDate.now();
    LocalDate next30Days = today.plusDays(30);
    int totalTrips = tripRepo.countTripsByUser(user);
    double totalBudget = tripRepo.getTotalBudgetByUser(user);
    int upcomingTrips = tripRepo.getTotalUpcomingTripsByUser(user, today, next30Days);
    double totalExpenses = expenseRepo.getTotalExpensesByUser(user);

    dashboard.setTotalTrips(totalTrips);
    dashboard.setTotalBudget(totalBudget);
    dashboard.setTotalExpenses(totalExpenses);
    dashboard.setRemainingBudget(totalBudget - totalExpenses);
    dashboard.setUpcomingTrips(upcomingTrips);
    return dashboard;
  }
}
