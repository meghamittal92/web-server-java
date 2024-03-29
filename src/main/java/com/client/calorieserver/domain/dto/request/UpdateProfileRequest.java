package com.client.calorieserver.domain.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

/**
 * Request object for update profile operation.
 */
@Data
public class UpdateProfileRequest {

	@Min(0)
	private Integer expectedCaloriesPerDay;

	@Email
	private String email;

}
