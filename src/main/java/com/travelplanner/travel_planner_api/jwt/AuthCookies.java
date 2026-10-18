package com.travelplanner.travel_planner_api.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookies {

  public static final String ACCESS_TOKEN = "access_token";

  private final boolean secure;

  public AuthCookies(@Value("${app.auth.cookie-secure}") boolean secure) {
    this.secure = secure;
  }

  // maxAge should equal the JWT's lifetime (JwtService.validityFor) so both expire together.
  public ResponseCookie create(String jwt, Duration maxAge) {
    return base(jwt).maxAge(maxAge).build();
  }

  // Same name + path, empty value, Max-Age=0: the only way to delete an HttpOnly cookie.
  public ResponseCookie clear() {
    return base("").maxAge(0).build();
  }

  private ResponseCookie.ResponseCookieBuilder base(String value) {
    return ResponseCookie.from(ACCESS_TOKEN, value)
            .httpOnly(true)   // invisible to document.cookie -> XSS can't steal it
            .secure(secure)   // HTTPS only (true in production)
            .sameSite("Lax")  // not sent on cross-site POST/PUT/DELETE -> first CSRF defence
            .path("/");       // keep "/": the frontend's dev proxy rewrites /api/users -> /users
  }
}
