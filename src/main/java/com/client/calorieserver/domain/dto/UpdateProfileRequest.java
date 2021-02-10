package com.client.calorieserver.domain.dto;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
public class UpdateProfileRequest {

    private Integer expectedCaloriesPerDay;
    private String email;
}
