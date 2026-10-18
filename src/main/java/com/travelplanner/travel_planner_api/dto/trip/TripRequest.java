package com.travelplanner.travel_planner_api.dto.trip;

import com.travelplanner.travel_planner_api.entities.TripType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class TripRequest {
  private String title;
  private String source;
  private String destination;
  private LocalDate startDate;
  private LocalDate endDate;
  private Double budget;
  private Integer travellers;
  private Set<TripType> tripTypes;
  private Set<String> invitedEmails;
}
