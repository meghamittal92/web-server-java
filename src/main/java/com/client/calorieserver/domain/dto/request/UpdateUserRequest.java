package com.client.calorieserver.domain.dto.request;

import com.client.calorieserver.domain.validator.PasswordConstraint;
import com.client.calorieserver.domain.validator.UsernameConstraint;
import com.sun.istack.Nullable;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Request object for update user operation.
 */
@Data
public class UpdateUserRequest {

	@UsernameConstraint
	private String username;

	@PasswordConstraint
	private String password;

	private Set<String> roles;

	@Min(0)
	private Integer expectedCaloriesPerDay;

	@Email
	private String email;

}
