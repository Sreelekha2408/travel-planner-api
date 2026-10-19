package com.travelplanner.travel_planner_api.service;

import com.travelplanner.travel_planner_api.dto.trip.TripRequest;
import com.travelplanner.travel_planner_api.entities.Trip;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.mapper.TripMapper;
import com.travelplanner.travel_planner_api.repo.ExpenseRepo;
import com.travelplanner.travel_planner_api.repo.ItineraryRepo;
import com.travelplanner.travel_planner_api.repo.TripRepo;
import com.travelplanner.travel_planner_api.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class TripService {

  // The currencies the app offers (same list as the frontend's currency picker).
  private static final String DEFAULT_CURRENCY = "INR";
  private static final Set<String> SUPPORTED_CURRENCIES =
          Set.of("INR", "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "SGD", "AED");

  @Autowired
  TripRepo tripRepo;

  @Autowired
  TripMapper tripMapper;

  @Autowired
  UserRepo userRepo;

  @Autowired
  ItineraryRepo itineraryRepo;

  @Autowired
  ExpenseRepo expenseRepo;

  public Trip createTrip(TripRequest tripRequest) {
    validate(tripRequest);
    String username = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();
    User user = userRepo.findByUsername(username);
    Trip trip = tripMapper.toEntity(tripRequest);
    trip.setCurrency(currencyOrDefault(tripRequest));
    trip.setUser(user);
    return tripRepo.save(trip);
  }

  public List<Trip> getUserTrips() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return tripRepo.findAllByUser_Username(username);
  }

  public Trip getTripById(int id) {
    return findOwnedTrip(id);
  }

  public Trip updateTrip(int id, TripRequest tripRequest) {
    validate(tripRequest);
    Trip trip = findOwnedTrip(id);
    trip.setTitle(tripRequest.getTitle());
    trip.setSource(tripRequest.getSource());
    trip.setDestination(tripRequest.getDestination());
    trip.setStartDate(tripRequest.getStartDate());
    trip.setEndDate(tripRequest.getEndDate());
    trip.setBudget(tripRequest.getBudget());
    trip.setCurrency(currencyOrDefault(tripRequest));
    trip.setTravellers(tripRequest.getTravellers());
    // Replace the contents (not the Set itself) so Hibernate tracks the change.
    trip.getTripTypes().clear();
    if (tripRequest.getTripTypes() != null) trip.getTripTypes().addAll(tripRequest.getTripTypes());
    trip.getInvitedEmails().clear();
    if (tripRequest.getInvitedEmails() != null) trip.getInvitedEmails().addAll(tripRequest.getInvitedEmails());
    return tripRepo.save(trip);
  }

  // Itinerary items and expenses reference the trip (foreign key), so they go first.
  // @Transactional: all three deletes succeed together or none do.
  @Transactional
  public void deleteTrip(int id) {
    Trip trip = findOwnedTrip(id);
    itineraryRepo.deleteAllByTrip_TripId(trip.getTripId());
    expenseRepo.deleteAllByTrip_TripId(trip.getTripId());
    tripRepo.delete(trip);
  }

  // 404 for a trip that belongs to someone else too, so the API doesn't reveal
  // which trip ids exist.
  private Trip findOwnedTrip(int id) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return tripRepo.findById(id)
            .filter(trip -> trip.getUser() != null && trip.getUser().getUsername().equals(username))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
  }

  // Trips sent without a currency use the app's default.
  private String currencyOrDefault(TripRequest tripRequest) {
    return tripRequest.getCurrency() == null ? DEFAULT_CURRENCY : tripRequest.getCurrency();
  }

  // Single-field rules (required, lengths, ranges, email format) are annotations on
  // TripRequest, checked by @Valid before this runs. These compare fields or use app data.
  private void validate(TripRequest tripRequest) {
    if (tripRequest.getEndDate().isBefore(tripRequest.getStartDate())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be on or after the start date");
    }
    if (tripRequest.getCurrency() != null && !SUPPORTED_CURRENCIES.contains(tripRequest.getCurrency())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Currency must be one of " + String.join(", ", SUPPORTED_CURRENCIES.stream().sorted().toList()));
    }
    Set<String> invites = tripRequest.getInvitedEmails();
    if (invites != null) {
      // Travellers is the headcount and invitees are travellers who also use the app, so the
      // count must cover the organiser plus everyone invited. Compares two fields, so it
      // lives here rather than in a field annotation.
      if (tripRequest.getTravellers() != null && tripRequest.getTravellers() < 1 + invites.size()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Travellers must include you and everyone invited");
      }
    }
  }
}
