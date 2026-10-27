package com.travelplanner.travel_planner_api.config;

import com.travelplanner.travel_planner_api.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  UserAuthService userAuthService;

  @Autowired
  JwtFilter jwtFilter;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            // CSRF must be ON: the browser now sends the auth cookie automatically, even on
            // requests another site triggers. Double-submit: Spring sets a readable XSRF-TOKEN
            // cookie (withHttpOnlyFalse) and the frontend echoes it in the X-XSRF-TOKEN header.
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                    // Spring rotates the token when it thinks a user just logged in. A stateless app
                    // re-authenticates from the cookie on EVERY request, so without this the token
                    // changes constantly and parallel requests fail with 403.
                    .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy()))
            .authorizeHttpRequests(request -> request
                    .requestMatchers("/users/signup", "/users/login", "/users/logout")
                    .permitAll()
                    // Boot forwards errors to /error; if that needed auth, every error would become a 401.
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Not logged in -> 401 (Spring's default here is 403, which the frontend
            // couldn't tell apart from "not allowed").
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider())
            .build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setPasswordEncoder(passwordEncoder());
    authProvider.setUserDetailsService(userAuthService);
    // After a successful sign-in, Spring asks the encoder whether the stored hash should be
    // redone (upgradeEncoding) and, if so, saves a new one through this service.
    authProvider.setUserDetailsPasswordService(userAuthService);
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
      return config.getAuthenticationManager();
  }

  // Each +1 doubles BCrypt's work. 10 is Spring's default and OWASP's minimum; 12 took ~2 s
  // per sign-in on the 0.1-CPU free host. The cost is stored in every hash ($2a$12$...),
  // so older cost-12 hashes still verify.
  private static final int BCRYPT_STRENGTH = 10;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(BCRYPT_STRENGTH) {
      // BCrypt's own check only flags hashes WEAKER than the current strength. Also flag the
      // older, slower cost-12 ones so they're redone at cost 10 on the user's next sign-in.
      @Override
      public boolean upgradeEncoding(String encodedPassword) {
        // A hash looks like $2a$10$<salt+hash>; the number between the $ signs is the cost.
        return encodedPassword != null && !encodedPassword.matches("^\\$2[aby]?\\$" + BCRYPT_STRENGTH + "\\$.*");
      }
    };
  }
}
