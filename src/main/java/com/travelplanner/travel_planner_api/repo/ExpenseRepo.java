package com.travelplanner.travel_planner_api.repo;

import com.travelplanner.travel_planner_api.entities.Expense;
import com.travelplanner.travel_planner_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Integer> {
  List<Expense> findAllByTrip_TripIdOrderByExpenseDateDescExpenseIdDesc(int tripId);

  void deleteAllByTrip_TripId(int tripId);

  @Query("""
SELECT SUM(e.amount) FROM Expense e where e.trip.user = :user""")
  Double getTotalExpensesByUser(User user);
}
