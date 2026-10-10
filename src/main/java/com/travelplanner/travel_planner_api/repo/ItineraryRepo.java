package com.travelplanner.travel_planner_api.repo;

import com.travelplanner.travel_planner_api.entities.ItineraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItineraryRepo extends JpaRepository<ItineraryItem, Integer> {

  List<ItineraryItem> findAllByTrip_TripIdAndTrip_User_Username(Integer tripId, String username);

  void deleteAllByTrip_TripId(int tripId);

}
