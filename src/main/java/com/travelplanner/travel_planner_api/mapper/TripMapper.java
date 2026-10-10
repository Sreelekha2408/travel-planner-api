package com.travelplanner.travel_planner_api.mapper;

import com.travelplanner.travel_planner_api.dto.trip.TripRequest;
import com.travelplanner.travel_planner_api.dto.trip.TripResponse;
import com.travelplanner.travel_planner_api.entities.Trip;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TripMapper {

  Trip toEntity(TripRequest tripRequest);

  TripResponse toResponse(Trip trip);
}