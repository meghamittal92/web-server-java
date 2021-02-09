package com.client.calorieserver.domain.dto;


import lombok.Data;



@Data
public class UpdateCalorieRequest {


    int numCalories;

    String mealDetails;
}
