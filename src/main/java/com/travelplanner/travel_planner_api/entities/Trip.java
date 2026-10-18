package com.travelplanner.travel_planner_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
  private Integer travellers;

  // Small value collections, each stored in its own table keyed by trip_id. Loaded eagerly
  // so the mapper can read them outside a transaction; Sets (not Lists) because Hibernate
  // can't eagerly fetch two Lists ("bags") at once.
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "trip_types", joinColumns = @JoinColumn(name = "trip_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "trip_type")
  private Set<TripType> tripTypes = new HashSet<>();

  // Saved only for now: sending invites and giving invitees access come with Shared trips.
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "trip_invites", joinColumns = @JoinColumn(name = "trip_id"))
  @Column(name = "email")
  private Set<String> invitedEmails = new HashSet<>();

  private String createdBy;
  private Timestamp createdOn;

  @ManyToOne
  @JoinColumn(name="user_id")
  private User user;
}
