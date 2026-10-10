package com.travelplanner.travel_planner_api.jwt;

import com.travelplanner.travel_planner_api.config.UserAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

  @Autowired
  JwtService jwtService;

  @Autowired
  UserAuthService userAuthService;
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // The JWT now arrives in the HttpOnly cookie instead of an Authorization header.
    Cookie cookie = WebUtils.getCookie(request, AuthCookies.ACCESS_TOKEN);
    if (cookie != null) {
      String token = cookie.getValue();
      if(SecurityContextHolder.getContext().getAuthentication() == null) {
        Boolean isTokenValid = jwtService.validateToken(token);
        if( isTokenValid) {
          String username = jwtService.extractUsername(token);
          UserDetails user = userAuthService.loadUserByUsername(username);
          Authentication authentication = new UsernamePasswordAuthenticationToken(
                  user, null, user.getAuthorities()
          );;
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
