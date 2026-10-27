package com.travelplanner.travel_planner_api.dto.user;

import com.travelplanner.travel_planner_api.dto.EmailAddresses;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

  // "Keep me signed in on this device": longer-lived cookie and JWT.
  private boolean rememberMe;

  // Jackson calls this setter while reading the JSON, so the lookup always uses the
  // normalized form (see EmailAddresses).
  public void setEmail(String email) {
    this.email = EmailAddresses.normalize(email);
  }
}
