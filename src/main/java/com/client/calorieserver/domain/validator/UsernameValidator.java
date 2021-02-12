package com.client.calorieserver.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<UsernameConstraint, String> {

	private static final int USERNAME_MAX_LENGTH = 256;

	/**
	 * ^ # start-of-string [A-Za-z0-9]+ # alphanumeric chars (?:[ _-][A-Za-z0-9]+)* #
	 * repetitions of _- followed by alphanumeric chars $ # end of string
	 */
	private static final String USERNAME_REGEX = "^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$";

	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {

		return username == null || (!username.isBlank() && username.matches(USERNAME_REGEX)
				&& (username.length() < USERNAME_MAX_LENGTH));
	}

}