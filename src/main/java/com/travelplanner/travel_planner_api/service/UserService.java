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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

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

  // Field rules (required, email format, password strength) are annotations on
  // UserRequestDTO, checked by @Valid in UserController before this runs.
  public User createUser(UserRequestDTO user) {
    // The sign-up form has no username field; the email is unique, so it doubles as one.
    if (!StringUtils.hasText(user.getUsername())) {
      user.setUsername(user.getEmail());
    }
    // Checked up front so the client gets a clear 409 instead of a 500 from the
    // database's unique constraint.
    if (userRepo.existsByEmail(user.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
    }
    if (userRepo.existsByUsername(user.getUsername())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "That username is taken");
    }
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

  public User getUserByEmail(String email) {
    return userRepo.findByEmail(email);
  }

  public User getUserDetails(String username) {
    return userRepo.findByUsername(username);
  }
}
