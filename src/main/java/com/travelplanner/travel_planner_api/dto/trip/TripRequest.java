package com.travelplanner.travel_planner_api.dto.trip;

import com.travelplanner.travel_planner_api.dto.ValidationPatterns;
import com.travelplanner.travel_planner_api.entities.TripType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

// Single-field rules live here and are checked by @Valid in TripController before the
// service runs. Rules that compare fields or use app data (end date after start date,
// supported currency, travellers covering everyone invited) stay in TripService.
@Data
public class TripRequest {

  @NotBlank(message = "Trip name is required")
  @Size(max = 100, message = "Trip name must be 100 characters or fewer")
  private String title;

  @Size(max = 255, message = "From must be 255 characters or fewer")
  private String source;

  @NotBlank(message = "Destination is required")
  @Size(max = 255, message = "Destination must be 255 characters or fewer")
  private String destination;

  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  private LocalDate endDate;

  @PositiveOrZero(message = "Budget can't be negative")
  private Double budget;

  private String currency;

  @Min(value = 1, message = "Travellers must be between 1 and 50")
  @Max(value = 50, message = "Travellers must be between 1 and 50")
  private Integer travellers;

  private Set<TripType> tripTypes;

  // The annotations inside <> apply to each email in the set.
  @Size(max = 20, message = "You can invite up to 20 people")
  private Set<@NotNull(message = "Invites must be valid email addresses")
          @Email(regexp = ValidationPatterns.EMAIL, message = "Invites must be valid email addresses") String> invitedEmails;
}
