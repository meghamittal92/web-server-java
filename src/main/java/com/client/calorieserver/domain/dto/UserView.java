package com.client.calorieserver.domain.dto;

import lombok.Data;

/**
 * Response object for User queries.
 */
@Data
public class UserView {

    private Long id;
    private String username;
    private Long expectedCaloriesPerDay;
}
