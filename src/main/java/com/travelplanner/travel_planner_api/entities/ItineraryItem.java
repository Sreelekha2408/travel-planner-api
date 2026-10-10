package com.travelplanner.travel_planner_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItineraryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int itineraryItemId;
  private String title;
  private String description;
  private String location;
  @Enumerated(EnumType.STRING)
  private ActivityType activityType;
  private LocalDate activityDate;
  private LocalTime activityTime;
  private double price;
  @ManyToOne
  @JoinColumn(name = "trip_id")
  private Trip trip;
}
