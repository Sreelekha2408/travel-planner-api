package com.travelplanner.travel_planner_api.dto.user;

import lombok.Data;

@Data
public class UserRequestDTO {
  private String username;
  private String email;
  private String password;
  private String firstName;
  private String lastName;
  private String mobile;
  private String picture;
}
