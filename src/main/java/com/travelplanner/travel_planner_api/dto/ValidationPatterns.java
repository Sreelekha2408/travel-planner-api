package com.travelplanner.travel_planner_api.dto;

// Regexes shared by request validation annotations, so every endpoint agrees.
public final class ValidationPatterns {

  // Same pattern the frontend uses (EMAIL_PATTERN in tripUtils.ts). Stricter than a bare
  // @Email, which accepts addresses like "a@b" with no domain suffix.
  public static final String EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

  // At least one letter and one digit; the length is checked by @Size.
  public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d).*$";

  private ValidationPatterns() {
  }
}
