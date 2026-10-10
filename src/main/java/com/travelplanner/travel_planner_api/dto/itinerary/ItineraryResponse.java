package com.travelplanner.travel_planner_api.dto.itinerary;

import com.travelplanner.travel_planner_api.entities.ActivityType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ItineraryResponse {
  private int itineraryItemId;
  private String title;
  private String description;
  private String location;
  private ActivityType activityType;
  private LocalDate activityDate;
  private LocalTime activityTime;
  private double price;
  private Integer tripId;
}
