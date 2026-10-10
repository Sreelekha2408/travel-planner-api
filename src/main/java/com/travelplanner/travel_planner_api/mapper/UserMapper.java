package com.travelplanner.travel_planner_api.mapper;

import com.travelplanner.travel_planner_api.dto.user.UserRequestDTO;
import com.travelplanner.travel_planner_api.dto.user.UserResponseDTO;
import com.travelplanner.travel_planner_api.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toEntity(UserRequestDTO userRequestDTO);

  UserResponseDTO toResponse(User user);
}