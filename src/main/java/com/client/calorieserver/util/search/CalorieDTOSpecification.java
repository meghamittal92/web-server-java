package com.client.calorieserver.util.search;

import com.client.calorieserver.configuration.Constants;
import com.client.calorieserver.domain.dto.db.*;
import com.client.calorieserver.domain.exception.InvalidSearchQueryException;
import com.client.calorieserver.domain.model.search.CalorieSearchKey;
import com.client.calorieserver.domain.model.search.Filter;
import com.client.calorieserver.domain.model.search.RelationalOperator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Specification class to build JPA Specification query for CalorieDTO
 */
public class CalorieDTOSpecification implements Specification<CalorieDTO> {

	private Filter criteria;

	private static final String PERCENT = "%";

	public CalorieDTOSpecification(final Filter criteria) {
		super();
		this.criteria = criteria;
	}

	@Override
	public Predicate toPredicate(final Root<CalorieDTO> root, final CriteriaQuery<?> query,
			final CriteriaBuilder builder) {
		try {
			final CalorieSearchKey calorieSearchKey = CalorieSearchKey.get(criteria.getKey());
			if (calorieSearchKey == null)
				throw new InvalidSearchQueryException("Unsupported search key");

			if (calorieSearchKey.equals(CalorieSearchKey.withinLimit)) {
				root.fetch(CalorieDTO_.caloriePerDayDTO);

				if (criteria.getOperator().equals(RelationalOperator.EQUAL)
						&& Boolean.valueOf(criteria.getValue().toString())
						|| criteria.getOperator().equals(RelationalOperator.NOT_EQUAL)
								&& !Boolean.valueOf(criteria.getValue().toString())) {
					return builder.lessThanOrEqualTo(
							root.get(CalorieDTO_.caloriePerDayDTO).get(CaloriePerDayDTO_.TOTAL_CALORIES),
							root.get(CalorieDTO_.userDTO).get(UserDTO_.expectedCaloriesPerDay));
				}
				else {
					return builder.greaterThan(
							root.get(CalorieDTO_.caloriePerDayDTO).get(CaloriePerDayDTO_.TOTAL_CALORIES),
							root.get(CalorieDTO_.userDTO).get(UserDTO_.expectedCaloriesPerDay));

				}

			}
			else if (root.get(calorieSearchKey.getDbColumnName()).getJavaType().isAssignableFrom(LocalDateTime.class)) {
				LocalDateTime value = LocalDateTime.parse(criteria.getValue().toString(),
						DateTimeFormatter.ofPattern(Constants.DateConstants.DATE_TIME_FORMAT));
				switch (criteria.getOperator()) {
				case EQUAL:
					return builder.equal(root.get(calorieSearchKey.getDbColumnName()).as(java.sql.Timestamp.class),
							java.sql.Timestamp.valueOf(value));
				case NOT_EQUAL:
					return builder.notEqual(root.get(calorieSearchKey.getDbColumnName()).as(java.sql.Timestamp.class),
							java.sql.Timestamp.valueOf(value));
				case GREATER_THAN:

					return builder.greaterThan(
							root.get(calorieSearchKey.getDbColumnName()).as(java.sql.Timestamp.class),
							java.sql.Timestamp.valueOf(value));
				case LESS_THAN:
					return builder.lessThan(root.get(calorieSearchKey.getDbColumnName()).as(java.sql.Timestamp.class),
							java.sql.Timestamp.valueOf(value));
				case GREATER_THAN_EQUAL_TO:

					return builder.greaterThanOrEqualTo(
							root.get(calorieSearchKey.getDbColumnName()).as(java.sql.Timestamp.class),
							java.sql.Timestamp.valueOf(value));
				case LESS_THAN_EQUAL_TO:
					return builder.lessThanOrEqualTo(
							root.get(calorieSearchKey.getDbColumnName()).as(java.sql.Timestamp.class),
							java.sql.Timestamp.valueOf(value));
				default:
					throw new InvalidSearchQueryException("Unsupported relational operator");
				}
			}
			else {
				switch (criteria.getOperator()) {
				case EQUAL:
					return builder.equal(root.get(calorieSearchKey.getDbColumnName()), castToRequiredType(
							root.get(calorieSearchKey.getDbColumnName()).getJavaType(), criteria.getValue()));
				case NOT_EQUAL:
					return builder.notEqual(root.get(calorieSearchKey.getDbColumnName()), castToRequiredType(
							root.get(calorieSearchKey.getDbColumnName()).getJavaType(), criteria.getValue()));
				case GREATER_THAN:
					return builder.greaterThan(root.get(calorieSearchKey.getDbColumnName()),
							castToRequiredType(root.get(calorieSearchKey.getDbColumnName()).getJavaType(),
									criteria.getValue()).toString());
				case LESS_THAN:
					return builder.lessThan(root.get(calorieSearchKey.getDbColumnName()),
							castToRequiredType(root.get(calorieSearchKey.getDbColumnName()).getJavaType(),
									criteria.getValue()).toString());
				case GREATER_THAN_EQUAL_TO:
					return builder.greaterThanOrEqualTo(root.get(calorieSearchKey.getDbColumnName()),
							castToRequiredType(root.get(calorieSearchKey.getDbColumnName()).getJavaType(),
									criteria.getValue()).toString());
				case LESS_THAN_EQUAL_TO:
					return builder.lessThanOrEqualTo(root.get(calorieSearchKey.getDbColumnName()),
							castToRequiredType(root.get(calorieSearchKey.getDbColumnName()).getJavaType(),
									criteria.getValue()).toString());
				case LIKE:
					return builder.like(root.get(calorieSearchKey.getDbColumnName()),
							castToRequiredType(root.get(calorieSearchKey.getDbColumnName()).getJavaType(),
									PERCENT + criteria.getValue()).toString() + PERCENT);
				default:
					throw new InvalidSearchQueryException("Unsupported relational operator");

				}
			}
		}
		catch (final IllegalArgumentException e) {
			throw new InvalidSearchQueryException(e.getMessage());

		}
	}

	private Object castToRequiredType(Class fieldType, Object value) {

		if (fieldType.isAssignableFrom(Integer.class)) {
			return Integer.valueOf(value.toString());
		}
		else if (fieldType.isAssignableFrom(Long.class)) {
			return Long.valueOf(value.toString());
		}
		return value.toString();
	}

}
