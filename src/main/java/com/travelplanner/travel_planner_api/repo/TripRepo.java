package com.travelplanner.travel_planner_api.repo;

import com.travelplanner.travel_planner_api.entities.Trip;
import com.travelplanner.travel_planner_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepo extends JpaRepository<Trip, Integer> {
  List<Trip> findAllByUser_Username(String username);
  int countTripsByUser(User user);
  @Query("""
       SELECT SUM(e.budget)
       FROM Trip e
       WHERE e.user = :user
       """)
  Double getTotalBudgetByUser(User user);

  @Query("""
SELECT COUNT(e) FROM Trip e where e.user = :user and e.startDate between :today and :next30Days
""")
  int getTotalUpcomingTripsByUser(User user, LocalDate today, LocalDate next30Days);
}
