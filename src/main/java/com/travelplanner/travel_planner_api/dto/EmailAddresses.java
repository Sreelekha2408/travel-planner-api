package com.travelplanner.travel_planner_api.dto;

import java.util.Locale;

// People type the same email in different ways (" Maya@Example.com "). Every email is
// stored and looked up in one form, so sign-in doesn't depend on capitals or stray spaces.
public final class EmailAddresses {

  public static String normalize(String email) {
    return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
  }

  private EmailAddresses() {
  }
}
