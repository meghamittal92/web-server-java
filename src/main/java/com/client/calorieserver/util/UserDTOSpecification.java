package com.client.calorieserver.util;

import com.client.calorieserver.domain.dto.db.*;
import com.client.calorieserver.domain.exception.InvalidSearchQueryException;
import com.client.calorieserver.domain.model.search.UserSearchKey;
import com.client.calorieserver.domain.model.search.Filter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;


public class UserDTOSpecification implements Specification<UserDTO> {

    private Filter criteria;
    private static final String PERCENT = "%";

    public UserDTOSpecification(final Filter criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(final Root<UserDTO> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
        try {
            final UserSearchKey userSearchKey = UserSearchKey.get(criteria.getKey());
            if (userSearchKey == null)
                throw new InvalidSearchQueryException("Unsupported search key");

            switch (criteria.getOperator()) {
                case EQUAL:
                    return builder.equal(root.get(userSearchKey.getDbColumnName()), castToRequiredType(
                            root.get(userSearchKey.getDbColumnName()).getJavaType(),
                            criteria.getValue()));
                case NOT_EQUAL:
                    return builder.notEqual(root.get(userSearchKey.getDbColumnName()), castToRequiredType(
                            root.get(userSearchKey.getDbColumnName()).getJavaType(),
                            criteria.getValue()));
                case GREATER_THAN:
                    return builder.greaterThan(root.get(userSearchKey.getDbColumnName()), castToRequiredType(
                            root.get(userSearchKey.getDbColumnName()).getJavaType(),
                            criteria.getValue()).toString());
                case LESS_THAN:
                    return builder.lessThan(root.get(userSearchKey.getDbColumnName()), castToRequiredType(
                            root.get(userSearchKey.getDbColumnName()).getJavaType(),
                            criteria.getValue()).toString());
                case GREATER_THAN_EQUAL_TO:
                    return builder.greaterThanOrEqualTo(root.get(userSearchKey.getDbColumnName()), castToRequiredType(
                            root.get(userSearchKey.getDbColumnName()).getJavaType(),
                            criteria.getValue()).toString());
                case LESS_THAN_EQUAL_TO:
                    return builder.lessThanOrEqualTo(root.get(userSearchKey.getDbColumnName()), castToRequiredType(
                            root.get(userSearchKey.getDbColumnName()).getJavaType(),
                            criteria.getValue()).toString());
                case LIKE:
                    return builder.like(root.get(userSearchKey.getDbColumnName()), castToRequiredType(
                            root.get(userSearchKey.getDbColumnName()).getJavaType(),
                            PERCENT + criteria.getValue()).toString() + PERCENT);
                default:
                    throw new InvalidSearchQueryException("Unsupported relational operator");


            }
        } catch (
                final IllegalArgumentException e) {
            throw new InvalidSearchQueryException(e.getMessage());

        }
    }

    private Object castToRequiredType(Class fieldType, Object value) {

        if (fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value.toString());
        }
        return value.toString();
    }

}
