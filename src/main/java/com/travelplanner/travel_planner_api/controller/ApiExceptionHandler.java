package com.travelplanner.travel_planner_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
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

  // A @Valid request body broke one or more annotation rules. Returns every problem at once:
  //   { "message": "Trip name is required. Budget can't be negative.",
  //     "fieldErrors": { "title": "Trip name is required", "budget": "Budget can't be negative" } }
  // "message" keeps the shape the frontend already shows; "fieldErrors" lets a form put each
  // message next to its field.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException e) {
    // The validator reports errors in no particular order; list them in the order the fields
    // are declared in the request class, which matches the form from top to bottom.
    Object target = e.getBindingResult().getTarget();
    List<String> fieldOrder = target == null ? List.of()
            : Arrays.stream(target.getClass().getDeclaredFields()).map(Field::getName).toList();
    List<FieldError> errors = e.getBindingResult().getFieldErrors().stream()
            .sorted(Comparator.comparingInt(error -> fieldOrder.indexOf(error.getField().replaceAll("\\[.*]$", ""))))
            .toList();

    Map<String, String> fieldErrors = new LinkedHashMap<>();
    for (FieldError error : errors) {
      // An email inside invitedEmails is reported as "invitedEmails[]"; use the field name.
      String field = error.getField().replaceAll("\\[.*]$", "");
      fieldErrors.putIfAbsent(field, error.getDefaultMessage()); // first problem per field
    }
    String message = String.join(". ", fieldErrors.values()) + ".";
    return Map.of("message", message, "fieldErrors", fieldErrors);
  }

  // Body that isn't valid JSON or has a wrongly typed value (e.g. "startDate": "soon", or an
  // unknown trip type). Without this the client gets a generic 400 with no message.
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleUnreadable(HttpMessageNotReadableException e) {
    return Map.of("message", "The request body is malformed or has a value of the wrong type");
  }
}
