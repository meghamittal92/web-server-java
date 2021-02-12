package com.client.calorieserver.domain.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Request object to hold user login params
 */
@Data
public class LoginRequest {

	@NotBlank
	private String username;

	@NotBlank
	private String password;

}
