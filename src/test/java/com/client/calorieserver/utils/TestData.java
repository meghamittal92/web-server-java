package com.client.calorieserver.utils;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.CaloriePerDayDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.db.UserDay;
import com.client.calorieserver.domain.model.Calorie;

import java.time.LocalDateTime;

public class TestData {
    public static final String default_email = "test_user@gmail.com";
    public static final String default_username = "test_user";
    public static final String default_password = "test_password$1";
    public static final Long default_calories = 10L;
    public static final String[] default_roles = {"USER"};
    protected static final String ADMIN_USERNAME = "admin";
    protected static final String ADMIN_PASSWORD = "secretpassword";

    public static final Long default_calorie_id = 1L;
    public static final Long default_calorie_user_id = 1L;
    public static final LocalDateTime default_date_time = LocalDateTime.now();
    public static final String default_calorie_detail = "first_meal";
    public static final Integer default_calorie_value = 50;
    public static final Integer default_calorie_per_day = 100;


    public static CalorieDTO getDefaultCalorieDTO(){
        CalorieDTO calorieDTO = new CalorieDTO();
        calorieDTO.setId(default_calorie_id);
        calorieDTO.setUserId(default_calorie_user_id);
        calorieDTO.setDateTime(default_date_time);
        calorieDTO.setMealDetails(default_calorie_detail);
        calorieDTO.setNumCalories(default_calorie_value);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(default_calorie_user_id);
        calorieDTO.setUserDTO(userDTO);
        return calorieDTO;
    }

    public static Calorie getDefaultCalorie(){
        Calorie calorie = new Calorie();
        calorie.setId(default_calorie_id);
        calorie.setUserId(default_calorie_user_id);
        calorie.setDateTime(default_date_time);
        calorie.setMealDetails(default_calorie_detail);
        calorie.setNumCalories(default_calorie_value);
        return calorie;
    }

    public static CaloriePerDayDTO getDefaultCaloriePerDayDTO(){
        CaloriePerDayDTO caloriePerDayDTO = new CaloriePerDayDTO();
        caloriePerDayDTO.setTotalCalories(default_calorie_per_day);
        UserDay userDay = new UserDay();
        userDay.setUserId(default_calorie_user_id);
        userDay.setDate(default_date_time.toLocalDate());
        caloriePerDayDTO.setId(userDay);
        return caloriePerDayDTO;
    }
}
