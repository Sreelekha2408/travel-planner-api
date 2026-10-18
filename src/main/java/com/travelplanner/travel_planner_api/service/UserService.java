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

import java.util.regex.Pattern;

@Service
public class UserService {

  private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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
    validateSignup(user);
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

  private void validateSignup(UserRequestDTO user) {
    if (!StringUtils.hasText(user.getFirstName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name is required");
    }
    if (user.getEmail() == null || !EMAIL.matcher(user.getEmail()).matches()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid email address");
    }
    String password = user.getPassword();
    if (password == null || password.length() < 8
            || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Use 8 or more characters with a mix of letters and numbers");
    }
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
