package com.travelplanner.travel_planner_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Trip {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int tripId;
  private String title;
  private String source;
  private String destination;
  private LocalDate startDate;
  private LocalDate endDate;
  private Double budget;
  private String createdBy;
  private Timestamp createdOn;

  @ManyToOne
  @JoinColumn(name="user_id")
  private User user;
}
