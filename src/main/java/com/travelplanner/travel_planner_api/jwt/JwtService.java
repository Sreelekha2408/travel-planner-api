package com.travelplanner.travel_planner_api.jwt;

import com.travelplanner.travel_planner_api.dto.user.UserLoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final Duration expiration;

  // The key comes from config, so tokens survive a restart and every instance agrees.
  public JwtService(@Value("${app.jwt.secret}") String secret,
                    @Value("${app.jwt.expiration}") Duration expiration) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.expiration = expiration;
  }

  public String generateToken(UserLoginRequest user) {
    Map<String,Object> claims = new HashMap<>();
    claims.put("roles", "USER");
    return Jwts.builder()
            .claims(claims)
            .subject(user.getEmail())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
            .signWith(secretKey)
            .compact();

  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
              .verifyWith(secretKey)
              .build()
              .parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String extractUsername(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
  }

}
