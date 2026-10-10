package com.travelplanner.travel_planner_api.controller;

import com.travelplanner.travel_planner_api.dto.dashboard.Dashboard;
import com.travelplanner.travel_planner_api.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

  @Autowired
  private DashboardService dashboardService;

  @GetMapping
  public Dashboard getDashboardDetails() {
    return dashboardService.getDashboardDetails();
  }
}
