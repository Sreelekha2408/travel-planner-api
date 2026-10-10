package com.travelplanner.travel_planner_api.controller;

import com.travelplanner.travel_planner_api.dto.user.UserLoginRequest;
import com.travelplanner.travel_planner_api.dto.user.UserRequestDTO;
import com.travelplanner.travel_planner_api.dto.user.UserResponseDTO;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.jwt.AuthCookies;
import com.travelplanner.travel_planner_api.mapper.UserMapper;
import com.travelplanner.travel_planner_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  UserMapper userMapper;

  @Autowired
  AuthCookies authCookies;

  @PostMapping("/signup")
  public UserResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO) {
    User user = userService.createUser(userRequestDTO);
    return userMapper.toResponse(user);
  }

  @PostMapping("/login")
  public ResponseEntity<UserResponseDTO> login(@RequestBody UserLoginRequest userLoginRequest) {
    String jwt = userService.verifyUser(userLoginRequest);
    User user = userService.getUserByEmail(userLoginRequest.getEmail());
    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authCookies.create(jwt).toString())
            .body(userMapper.toResponse(user)); // the token is NOT in the body
  }

  // JS can't read the HttpOnly cookie, so the frontend asks who is logged in.
  // Reached only with a valid cookie; otherwise the entry point answers 401.
  @GetMapping("/me")
  public UserResponseDTO me(Authentication authentication) {
    return userMapper.toResponse(userService.getUserDetails(authentication.getName()));
  }

  // Only the server can remove an HttpOnly cookie.
  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, authCookies.clear().toString())
            .build();
  }

  @GetMapping("/details/{username}")
  public UserResponseDTO getUserDetails(@PathVariable String username) {
    User user = userService.getUserDetails(username);
    return userMapper.toResponse(user);
  }
}
