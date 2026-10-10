package com.travelplanner.travel_planner_api.jwt;

import com.travelplanner.travel_planner_api.dto.user.UserLoginRequest;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

  private final SecretKey secretKey = generateKey();

  public String generateToken(UserLoginRequest user) {
    Map<String,Object> claims = new HashMap<>();
    claims.put("roles", "USER");
    return Jwts.builder()
            .claims(claims)
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
            .signWith(secretKey)
            .compact();

  }

  public SecretKey generateKey() {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
      keyGenerator.init(256);
      Key key = keyGenerator.generateKey();
      return (SecretKey) key;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
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
