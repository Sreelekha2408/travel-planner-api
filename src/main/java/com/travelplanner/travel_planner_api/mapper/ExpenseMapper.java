package com.travelplanner.travel_planner_api.mapper;

import com.travelplanner.travel_planner_api.dto.expenses.ExpenseRequest;
import com.travelplanner.travel_planner_api.dto.expenses.ExpenseResponse;
import com.travelplanner.travel_planner_api.entities.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
  @Mapping(source = "trip.tripId", target = "tripId")
  ExpenseResponse toResponse(Expense expense);
  Expense toEntity(ExpenseRequest expenseRequest);
}
