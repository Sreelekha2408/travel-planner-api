package com.travelplanner.travel_planner_api.service;

import com.travelplanner.travel_planner_api.dto.user.UserLoginRequest;
import com.travelplanner.travel_planner_api.dto.user.UserRequestDTO;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.jwt.JwtService;
import com.travelplanner.travel_planner_api.mapper.UserMapper;
import com.travelplanner.travel_planner_api.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Autowired
  UserRepo userRepo;

  @Autowired
  UserMapper userMapper;

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  private JwtService jwtService;

  @Autowired
  PasswordEncoder encoder;

  public User createUser(UserRequestDTO user) {
    user.setPassword(encoder.encode(user.getPassword()));
    return userRepo.save(userMapper.toEntity(user));
  }

  // authenticate() throws BadCredentialsException for a wrong/blank email or password;
  // it is no longer swallowed, so ApiExceptionHandler can turn it into a 401.
  public String verifyUser(UserLoginRequest userLoginRequest) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            userLoginRequest.getEmail(),
            userLoginRequest.getPassword()
    ));
    return jwtService.generateToken(userLoginRequest);
  }

  public User getUserDetails(String username) {
    return userRepo.findByUsername(username);
  }
}
