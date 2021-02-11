package com.client.calorieserver.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements
        ConstraintValidator<PasswordConstraint, String> {

    private static final int PASSWORD_MAX_LENGTH = 256;
    /**
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-zA-Z])    # a letter must occur at least once
     * (?=.*[!@#$%^&+=])  # a special character must occur at least once
     * (?=\S+$)          # no whitespace allowed in the entire string
     * .{8,}             # anything, at least eight places though
     * $                 # end-of-string
     */
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";

    @Override
    public boolean isValid(String password,
                           ConstraintValidatorContext context) {

        //After matching one or more alphanumeric characters, if there's a separator it must be followed by one or more alphanumerics
        return password == null || (!password.isBlank() && password.matches(PASSWORD_REGEX)
                && (password.length() < PASSWORD_MAX_LENGTH));
    }

}