package com.travelplanner.travel_planner_api.controller;

import com.travelplanner.travel_planner_api.dto.trip.TripRequest;
import com.travelplanner.travel_planner_api.dto.trip.TripResponse;
import com.travelplanner.travel_planner_api.entities.Trip;
import com.travelplanner.travel_planner_api.mapper.TripMapper;
import com.travelplanner.travel_planner_api.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/trip")
public class TripController {

  @Autowired
  TripService tripService;

  @Autowired
  TripMapper tripMapper;

  @PostMapping("/create")
  public TripResponse createTrip(@RequestBody TripRequest tripRequest) {
    Trip trip = tripService.createTrip(tripRequest);
    return tripMapper.toResponse(trip);
  }

  @GetMapping("/list")
  public List<TripResponse> getUserTrips() {
    List<Trip> trips = tripService.getUserTrips();
    return trips.stream().map(tripMapper::toResponse).toList();
  }

  @GetMapping("/{id}")
  public TripResponse getTripById(@PathVariable("id") int id) {
    Trip trip = tripService.getTripById(id);
    return tripMapper.toResponse(trip);
  }

  @PutMapping("/{id}")
  public TripResponse updateTrip(@PathVariable("id") int id, @RequestBody TripRequest tripRequest) {
    Trip trip = tripService.updateTrip(id, tripRequest);
    return tripMapper.toResponse(trip);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTrip(@PathVariable("id") int id) {
    tripService.deleteTrip(id);
  }
}
