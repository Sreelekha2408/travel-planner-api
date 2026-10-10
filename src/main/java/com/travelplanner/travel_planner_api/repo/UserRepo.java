package com.travelplanner.travel_planner_api.repo;

import com.travelplanner.travel_planner_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer > {
  User findByUsername(String username);
}
