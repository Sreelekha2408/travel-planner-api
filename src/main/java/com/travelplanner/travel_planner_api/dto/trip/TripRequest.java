package com.travelplanner.travel_planner_api.dto.trip;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TripRequest {
  private String title;
  private String source;
  private String destination;
  private LocalDate startDate;
  private LocalDate endDate;
  private Double budget;
}
