package com.travelplanner.travel_planner_api.controller;

import com.travelplanner.travel_planner_api.dto.itinerary.ItineraryRequest;
import com.travelplanner.travel_planner_api.dto.itinerary.ItineraryResponse;
import com.travelplanner.travel_planner_api.entities.ItineraryItem;
import com.travelplanner.travel_planner_api.mapper.ItineraryMapper;
import com.travelplanner.travel_planner_api.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/itineraries")
public class ItineraryController {

  @Autowired
  ItineraryService itineraryService;

  @Autowired
  ItineraryMapper itineraryMapper;

  @PostMapping("/{tripId}")
  public List<ItineraryResponse> createItinerary(@PathVariable int tripId, @RequestBody List<ItineraryRequest> itineraryRequests) {
    List<ItineraryItem> itineraryItems = itineraryService.createItineraries(tripId, itineraryRequests);
    List<ItineraryResponse> itineraryResponses = new ArrayList<>();
    for (ItineraryItem itineraryItem : itineraryItems) {
      itineraryResponses.add(itineraryMapper.toResponse(itineraryItem));
    }
    return itineraryResponses;
  }

  @GetMapping("/{tripId}")
  public List<ItineraryResponse> getAllItineraries(@PathVariable int tripId) {
    List<ItineraryItem> itineraryItems = itineraryService.getAllItineraries(tripId);
    List<ItineraryResponse> itineraryResponses = new ArrayList<>();
    for (ItineraryItem itineraryItem : itineraryItems) {
      itineraryResponses.add(itineraryMapper.toResponse(itineraryItem));
    }
    return itineraryResponses;
  }

  @PutMapping("/{itineraryId}")
  public ItineraryResponse updateItinerary(@PathVariable int itineraryId, @RequestBody ItineraryRequest itineraryRequest) {
    ItineraryItem item = itineraryService.updateItinerary(itineraryId, itineraryRequest);
    return itineraryMapper.toResponse(item);
  }

  @DeleteMapping("/{itineraryId}")
  public String deleteItinerary(@PathVariable int itineraryId) {
    return itineraryService.deleteItinerary(itineraryId);
  }

  @Transactional
  @DeleteMapping("/all/{tripId}")
  public String deleteAllItineraries(@PathVariable int tripId) {
    return itineraryService.deleteAllItineraries(tripId);
  }
}
