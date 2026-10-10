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
import org.springframework.security.core.Authentication;
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

  public String verifyUser(UserLoginRequest userLoginRequest) {
    try {
      if(userLoginRequest.getEmail() == null || userLoginRequest.getPassword() == null) {
        throw new Exception("Email/Password can't be blank");
      } else {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userLoginRequest.getEmail(),
                userLoginRequest.getPassword()
        );
        Authentication userAuth = authenticationManager.authenticate(authentication);
        if(userAuth.isAuthenticated()) {
          return jwtService.generateToken(userLoginRequest);
        } else {
          throw new Exception("Invalid username or password");
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return "";
  }

  public User getUserDetails(String username) {
    return userRepo.findByUsername(username);
  }
}
