package com.client.calorieserver.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements
        ConstraintValidator<UsernameConstraint, String> {

    private static final int USERNAME_MAX_LENGTH = 256;
    private static final String USERNAME_REGEX = "^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$";
    @Override
    public void initialize(UsernameConstraint usernameConstraint) {
    }

    @Override
    public boolean isValid(String username,
                           ConstraintValidatorContext context) {

        //After matching one or more alphanumeric characters, if there's a separator it must be followed by one or more alphanumerics
        return username == null || (!username.isBlank() && username.matches(USERNAME_REGEX)
                && (username.length() < USERNAME_MAX_LENGTH));
    }

}