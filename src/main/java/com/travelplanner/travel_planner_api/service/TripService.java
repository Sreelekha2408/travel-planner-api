package com.travelplanner.travel_planner_api.service;

import com.travelplanner.travel_planner_api.dto.trip.TripRequest;
import com.travelplanner.travel_planner_api.entities.Trip;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.mapper.TripMapper;
import com.travelplanner.travel_planner_api.repo.TripRepo;
import com.travelplanner.travel_planner_api.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {

  @Autowired
  TripRepo tripRepo;

  @Autowired
  TripMapper tripMapper;

  @Autowired
  UserRepo userRepo;

  public Trip createTrip(TripRequest tripRequest) {
    String username = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();
    User user = userRepo.findByUsername(username);
    Trip trip = tripMapper.toEntity(tripRequest);
    trip.setUser(user);
    return tripRepo.save(trip);
  }

  public List<Trip> getUserTrips() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepo.findByUsername(username);
    return tripRepo.findAllByUser_Username(username);
  }

  public Trip getTripById(int id) {
    Optional<Trip> trip = tripRepo.findById(id);
    try {
      if(trip.isPresent()) return trip.get();
      throw new Exception("Trip not found");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return null;
  }
}
