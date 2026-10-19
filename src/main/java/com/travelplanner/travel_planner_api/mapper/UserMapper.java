package com.travelplanner.travel_planner_api.mapper;

import com.travelplanner.travel_planner_api.dto.user.SignUpRequest;
import com.travelplanner.travel_planner_api.dto.user.UserResponse;
import com.travelplanner.travel_planner_api.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toEntity(SignUpRequest signUpRequest);

  UserResponse toResponse(User user);
}