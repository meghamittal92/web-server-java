package com.client.calorieserver.mapper;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.mapper.CalorieMapperImpl;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CalorieMapperImplTest {
    private static final int default_calories = 20;
    private static final LocalDateTime calorie_time = LocalDateTime.now()
            .truncatedTo(ChronoUnit.SECONDS);
    private static final Long default_calorie_id = 1L;
    private CalorieMapperImpl calorieMapper;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        calorieMapper = new CalorieMapperImpl();
    }

    @Test
    void afterMappingCalorie(){
        CalorieDTO calorieDTO = TestData.getDefaultCalorieDTO();
        calorieDTO.getUserDTO().setExpectedCaloriesPerDay(
                TestData.getDefaultCaloriePerDayDTO().getTotalCalories());
        calorieDTO.setCaloriePerDayDTO(TestData.getDefaultCaloriePerDayDTO());
        Calorie calorie = TestData.getDefaultCalorie();
        calorie.setTotalCaloriesForDay(2*calorieDTO.getNumCalories());
        calorieMapper.afterMappingCalorie(calorieDTO, calorie);
        assert calorie.getNumCalories().equals(calorieDTO.getNumCalories());
        assert calorie.isWithinLimit();

        calorieDTO = TestData.getDefaultCalorieDTO();
        calorieDTO.getUserDTO().setExpectedCaloriesPerDay(
                calorieDTO.getNumCalories()/2);
        calorieDTO.setCaloriePerDayDTO(TestData.getDefaultCaloriePerDayDTO());
        calorie = TestData.getDefaultCalorie();
        calorie.setTotalCaloriesForDay(2*calorieDTO.getNumCalories());
        calorieMapper.afterMappingCalorie(calorieDTO, calorie);
        assert calorie.getNumCalories().equals(calorieDTO.getNumCalories());
        assert !calorie.isWithinLimit();
    }

}
