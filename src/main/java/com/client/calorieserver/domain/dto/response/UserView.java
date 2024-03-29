package com.client.calorieserver.domain.dto.response;

import com.client.calorieserver.domain.model.Role;
import lombok.Data;

import java.util.Set;

/**
 * Complete view of a user.
 */
@Data
public class UserView {

	private Long id;

	private String username;

	private Integer expectedCaloriesPerDay;

	private Set<Role> roles;

	private String email;

}
