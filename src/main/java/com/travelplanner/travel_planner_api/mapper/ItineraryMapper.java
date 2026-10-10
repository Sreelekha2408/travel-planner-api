package com.travelplanner.travel_planner_api.mapper;

import com.travelplanner.travel_planner_api.dto.itinerary.ItineraryRequest;
import com.travelplanner.travel_planner_api.dto.itinerary.ItineraryResponse;
import com.travelplanner.travel_planner_api.entities.ItineraryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItineraryMapper {
  ItineraryItem toEntity(ItineraryRequest itineraryRequest);

  @Mapping(source = "trip.tripId", target = "tripId")
  ItineraryResponse toResponse(ItineraryItem itineraryItem);
}
