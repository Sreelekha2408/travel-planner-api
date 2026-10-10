package com.travelplanner.travel_planner_api.controller;

import com.travelplanner.travel_planner_api.dto.user.UserLoginRequest;
import com.travelplanner.travel_planner_api.dto.user.UserRequestDTO;
import com.travelplanner.travel_planner_api.dto.user.UserResponseDTO;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.mapper.UserMapper;
import com.travelplanner.travel_planner_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  UserMapper userMapper;

  @PostMapping("/signup")
  public UserResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO) {
    User user = userService.createUser(userRequestDTO);
    return userMapper.toResponse(user);
  }

  @PostMapping("/login")
  public String login(@RequestBody UserLoginRequest userLoginRequest) {
    return userService.verifyUser(userLoginRequest);
  }

  @GetMapping("/details/{username}")
  public UserResponseDTO getUserDetails(@PathVariable String username) {
    User user = userService.getUserDetails(username);
    return userMapper.toResponse(user);
  }
}
