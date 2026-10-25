package com.travelplanner.travel_planner_api.service;

import com.travelplanner.travel_planner_api.dto.itinerary.ItineraryRequest;
import com.travelplanner.travel_planner_api.entities.ItineraryItem;
import com.travelplanner.travel_planner_api.entities.Trip;
import com.travelplanner.travel_planner_api.mapper.ItineraryMapper;
import com.travelplanner.travel_planner_api.repo.ItineraryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

// Every method first checks the trip (or the item's trip) belongs to the signed-in user.
// Someone else's trip or item gets the same 404 as a missing one, so ids can't be probed.
@Service
public class ItineraryService {
  @Autowired
  ItineraryRepo itineraryRepo;

  @Autowired
  ItineraryMapper itineraryMapper;

  @Autowired
  TripService tripService;

  public List<ItineraryItem> createItineraries(int tripId, List<ItineraryRequest> itineraryRequests) {
    Trip trip = tripService.getTripById(tripId); // 404 unless it's yours
    List<ItineraryItem> itineraries = new ArrayList<>();
    for (ItineraryRequest itineraryRequest : itineraryRequests) {
      ItineraryItem itineraryItem = itineraryMapper.toEntity(itineraryRequest);
      itineraryItem.setTrip(trip);
      itineraries.add(itineraryItem);
    }
    return itineraryRepo.saveAll(itineraries);
  }

  public List<ItineraryItem> getAllItineraries(int tripId) {
    Trip trip = tripService.getTripById(tripId);
    return itineraryRepo.findAllByTrip_TripIdAndTrip_User_Username(trip.getTripId(), currentUsername());
  }

  public ItineraryItem updateItinerary(int itineraryId, ItineraryRequest itineraryRequest) {
    ItineraryItem itineraryItem = findOwnedItem(itineraryId);
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
    itineraryRepo.delete(findOwnedItem(itineraryId));
    return "Itinerary deleted";
  }

  // A derived delete query needs a transaction.
  @Transactional
  public String deleteAllItineraries(int tripId) {
    Trip trip = tripService.getTripById(tripId);
    itineraryRepo.deleteAllByTrip_TripId(trip.getTripId());
    return "Itineraries deleted";
  }

  private ItineraryItem findOwnedItem(int itineraryId) {
    String username = currentUsername();
    return itineraryRepo.findById(itineraryId)
            .filter(item -> item.getTrip() != null && item.getTrip().getUser() != null
                    && item.getTrip().getUser().getUsername().equals(username))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Itinerary item not found"));
  }

  private String currentUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
