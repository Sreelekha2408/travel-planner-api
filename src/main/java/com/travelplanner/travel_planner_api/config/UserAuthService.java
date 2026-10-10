package com.travelplanner.travel_planner_api.config;

import com.travelplanner.travel_planner_api.dto.user.UserPrincipal;
import com.travelplanner.travel_planner_api.entities.User;
import com.travelplanner.travel_planner_api.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService {

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
}
