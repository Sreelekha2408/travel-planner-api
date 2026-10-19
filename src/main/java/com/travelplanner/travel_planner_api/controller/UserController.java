package com.travelplanner.travel_planner_api.controller;

import com.travelplanner.travel_planner_api.dto.user.LoginRequest;
import com.travelplanner.travel_planner_api.dto.user.SignUpRequest;
import com.travelplanner.travel_planner_api.dto.user.UserResponse;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.jwt.AuthCookies;
import com.travelplanner.travel_planner_api.jwt.JwtService;
import com.travelplanner.travel_planner_api.mapper.UserMapper;
import com.travelplanner.travel_planner_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping(value = "/users")
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  UserMapper userMapper;

  @Autowired
  AuthCookies authCookies;

  @Autowired
  JwtService jwtService;

  @PostMapping("/signup")
  public UserResponse createUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    User user = userService.createUser(signUpRequest);
    return userMapper.toResponse(user);
  }

  @PostMapping("/login")
  public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    String jwt = userService.verifyUser(loginRequest);
    User user = userService.getUserByEmail(loginRequest.getEmail());
    Duration validity = jwtService.validityFor(loginRequest.isRememberMe());
    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authCookies.create(jwt, validity).toString())
            .body(userMapper.toResponse(user)); // the token is NOT in the body
  }

  // JS can't read the HttpOnly cookie, so the frontend asks who is logged in.
  // Reached only with a valid cookie; otherwise the entry point answers 401.
  @GetMapping("/me")
  public UserResponse me(Authentication authentication) {
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
  public UserResponse getUserDetails(@PathVariable String username) {
    User user = userService.getUserDetails(username);
    return userMapper.toResponse(user);
  }
}
