package com.travelplanner.travel_planner_api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

// From the Spring Security 6 reference ("Single-Page Applications").
// Spring Security 7 replaces this class with csrf.spa().
public final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
    // BREACH protection for server-rendered pages that embed the token.
    this.xor.handle(request, response, csrfToken);
    // Spring loads the token lazily; loading it here makes the XSRF-TOKEN cookie
    // appear on the first response, so the SPA has a value to send back.
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    // The SPA sends the raw cookie value in the X-XSRF-TOKEN header -> compare it as-is.
    String headerValue = request.getHeader(csrfToken.getHeaderName());
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request, csrfToken);
  }
}
