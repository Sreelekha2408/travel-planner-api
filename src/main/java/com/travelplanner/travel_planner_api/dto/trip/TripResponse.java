package com.travelplanner.travel_planner_api.dto.trip;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class TripResponse {
  private int tripId;
  private String title;
  private String source;
  private String destination;
  private LocalDate startDate;
  private LocalDate endDate;
  private Double budget;
  private String createdBy;
  private Timestamp createdOn;
}
