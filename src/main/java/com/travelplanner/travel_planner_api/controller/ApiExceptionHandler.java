package com.travelplanner.travel_planner_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  // Same message for "no such email" and "wrong password", so nobody can probe which emails exist.
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, String> handleAuthentication(AuthenticationException e) {
    return Map.of("message", "Invalid email or password");
  }

  // Spring Boot hides the reason of a ResponseStatusException by default; return it as
  // { message } so the frontend can show e.g. "End date must be on or after the start date".
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException e) {
    String message = e.getReason() != null ? e.getReason() : "Request failed";
    return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", message));
  }
}
