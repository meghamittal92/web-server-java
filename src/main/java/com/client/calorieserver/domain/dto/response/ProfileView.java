package com.client.calorieserver.domain.dto.response;


import lombok.Data;


/**
 * Profile details of a user.
 */
@Data
public class ProfileView {

    private String username;
    private Integer expectedCaloriesPerDay;
    private String email;
}
