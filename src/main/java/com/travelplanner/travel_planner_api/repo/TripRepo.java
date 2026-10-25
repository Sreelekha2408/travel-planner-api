package com.travelplanner.travel_planner_api.repo;

import com.travelplanner.travel_planner_api.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepo extends JpaRepository<Trip, Integer> {
  List<Trip> findAllByUser_Username(String username);
}
