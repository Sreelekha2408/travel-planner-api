package com.travelplanner.travel_planner_api.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
  private int userId;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String mobile;
  private String picture;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
