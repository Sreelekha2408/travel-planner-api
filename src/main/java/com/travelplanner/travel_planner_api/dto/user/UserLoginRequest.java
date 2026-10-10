package com.travelplanner.travel_planner_api.dto.user;

import lombok.Data;

@Data
public class UserLoginRequest {
  private String username;
  private String password;
}
