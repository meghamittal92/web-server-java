package com.client.calorieserver.domain.exception;

import lombok.Getter;

/**
 * This exception is thrown when there is an attemp to create an
 * already existing entity.
 */
@Getter
public class EntityAlreadyExistsException extends RuntimeException {


    private Class<?> entityClass;

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException(Class<?> clazz, long id) {
        super(String.format("Entity %s with id %d already exists", clazz.getSimpleName(), id));
        this.entityClass = clazz;

    }

    public EntityAlreadyExistsException(Class<?> clazz, String message) {
        super(message);
        this.entityClass = clazz;
    }
}
