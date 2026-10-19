package com.travelplanner.travel_planner_api.dto.user;

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
}
