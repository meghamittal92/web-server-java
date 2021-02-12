package com.client.calorieserver.domain.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameConstraint {

	static final String ERROR_MESSAGE = "Invalid username. " + "Only alphanumeric chars and - and _ are allowed. "
			+ "Every special char should be preceded and followed by an alphanumeric. " + "Max length 255";

	String message() default ERROR_MESSAGE;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
