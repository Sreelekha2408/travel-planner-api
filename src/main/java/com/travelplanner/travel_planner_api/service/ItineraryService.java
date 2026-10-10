package com.travelplanner.travel_planner_api.service;

import com.travelplanner.travel_planner_api.dto.itinerary.ItineraryRequest;
import com.travelplanner.travel_planner_api.entities.ItineraryItem;
import com.travelplanner.travel_planner_api.entities.Trip;
import com.travelplanner.travel_planner_api.mapper.ItineraryMapper;
import com.travelplanner.travel_planner_api.repo.ItineraryRepo;
import com.travelplanner.travel_planner_api.repo.TripRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItineraryService {
  @Autowired
  ItineraryRepo itineraryRepo;

  @Autowired
  ItineraryMapper itineraryMapper;

  @Autowired
  TripRepo tripRepo;

  public List<ItineraryItem> createItineraries(int tripId, List<ItineraryRequest> itineraryRequests) {
      List<ItineraryItem> itineraries = new ArrayList<>();
      Optional<Trip> trip = tripRepo.findById(tripId);
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      if (trip.isPresent() && trip.get().getUser().getUsername().equals(username)) {
        for (ItineraryRequest itineraryRequest : itineraryRequests) {
          ItineraryItem itineraryItem = itineraryMapper.toEntity(itineraryRequest);
          itineraryItem.setTrip(trip.get());
          itineraries.add(itineraryItem);
        }
        return itineraryRepo.saveAll(itineraries);
      } else {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access");
      }
  }

  public List<ItineraryItem> getAllItineraries(Integer tripId) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return itineraryRepo.findAllByTrip_TripIdAndTrip_User_Username(tripId, username);
  }

  public ItineraryItem updateItinerary(int itineraryId, ItineraryRequest itineraryRequest) {
    ItineraryItem itineraryItem = itineraryRepo.findById(itineraryId).orElseThrow();
    itineraryItem.setTitle(itineraryRequest.getTitle());
    itineraryItem.setDescription(itineraryRequest.getDescription());
    itineraryItem.setLocation(itineraryRequest.getLocation());
    itineraryItem.setActivityType(itineraryRequest.getActivityType());
    itineraryItem.setActivityDate(itineraryRequest.getActivityDate());
    itineraryItem.setActivityTime(itineraryRequest.getActivityTime());
    itineraryItem.setPrice(itineraryRequest.getPrice());

    return itineraryRepo.save(itineraryItem);
  }

  public String deleteItinerary(int itineraryId) {
    itineraryRepo.deleteById(itineraryId);
    return "Itinerary deleted";
  }

  public String deleteAllItineraries(int tripId) {
    itineraryRepo.deleteAllByTrip_TripId(tripId);
    return "Itineraries deleted";
  }
}
