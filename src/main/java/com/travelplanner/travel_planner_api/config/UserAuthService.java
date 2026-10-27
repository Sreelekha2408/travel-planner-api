package com.travelplanner.travel_planner_api.config;

import com.travelplanner.travel_planner_api.dto.user.UserPrincipal;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService, UserDetailsPasswordService {

  @Autowired
  UserRepo userRepo;
  // Spring calls the login identifier "username"; ours is the email. UserPrincipal still
  // returns the real username, so getAuthentication().getName() in services is unchanged.
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepo.findByEmail(email);
    if(user == null) {
      throw new UsernameNotFoundException("User not found " + email);
    }
    return new UserPrincipal(user);
  }

  // Called by Spring after a successful sign-in when the stored hash uses an older BCrypt
  // cost (see SecurityConfig.passwordEncoder): saves the same password re-hashed at the
  // current cost. The user notices nothing; only their next sign-ins get faster.
  @Override
  public UserDetails updatePassword(UserDetails userDetails, String newEncodedPassword) {
    User user = userRepo.findByUsername(userDetails.getUsername());
    user.setPassword(newEncodedPassword);
    return new UserPrincipal(userRepo.save(user));
  }
}
