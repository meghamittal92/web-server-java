package com.client.calorieserver.repository;

import com.client.calorieserver.domain.dto.db.CaloriePerDayDTO;
import com.client.calorieserver.domain.dto.db.UserDay;
import org.springframework.data.repository.CrudRepository;

public interface CaloriePerDayRepository extends CrudRepository<CaloriePerDayDTO, UserDay> {
}
