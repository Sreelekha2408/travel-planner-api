package com.travelplanner.travel_planner_api.dto.itinerary;

import com.travelplanner.travel_planner_api.entities.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ItineraryRequest {

  @NotBlank(message = "Title is required")
  @Size(max = 255, message = "Title must be 255 characters or fewer")
  private String title;

  @Size(max = 255, message = "Description must be 255 characters or fewer")
  private String description;

  @Size(max = 255, message = "Location must be 255 characters or fewer")
  private String location;

  @NotNull(message = "Activity type is required")
  private ActivityType activityType;

  @NotNull(message = "Date is required")
  private LocalDate activityDate;

  private LocalTime activityTime; // optional: some plans have no set time

  @PositiveOrZero(message = "Price can't be negative")
  private double price;
}
