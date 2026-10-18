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
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class TripService {

  // Same pattern the frontend uses.
  // The currencies the app offers (same list as the frontend's currency picker).
  private static final String DEFAULT_CURRENCY = "INR";
  private static final Set<String> SUPPORTED_CURRENCIES =
          Set.of("INR", "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "SGD", "AED");
  private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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

  private void validate(TripRequest tripRequest) {
    if (!StringUtils.hasText(tripRequest.getTitle()) || !StringUtils.hasText(tripRequest.getDestination())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip name and destination are required");
    }
    if (tripRequest.getStartDate() == null || tripRequest.getEndDate() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end dates are required");
    }
    if (tripRequest.getEndDate().isBefore(tripRequest.getStartDate())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be on or after the start date");
    }
    if (tripRequest.getBudget() != null && tripRequest.getBudget() < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Budget can't be negative");
    }
    if (tripRequest.getCurrency() != null && !SUPPORTED_CURRENCIES.contains(tripRequest.getCurrency())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Currency must be one of " + String.join(", ", SUPPORTED_CURRENCIES.stream().sorted().toList()));
    }
    if (tripRequest.getTravellers() != null && (tripRequest.getTravellers() < 1 || tripRequest.getTravellers() > 50)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Travellers must be between 1 and 50");
    }
    Set<String> invites = tripRequest.getInvitedEmails();
    if (invites != null) {
      if (invites.size() > 20) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can invite up to 20 people");
      }
      if (invites.stream().anyMatch(email -> email == null || !EMAIL.matcher(email).matches())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invites must be valid email addresses");
      }
    }
  }
}
