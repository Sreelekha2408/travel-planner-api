package com.travelplanner.travel_planner_api.jwt;

import com.travelplanner.travel_planner_api.config.UserAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

  @Autowired
  JwtService jwtService;

  @Autowired
  UserAuthService userAuthService;
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.replace("Bearer ", "");
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
