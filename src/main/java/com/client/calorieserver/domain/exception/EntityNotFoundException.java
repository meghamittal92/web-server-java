package com.client.calorieserver.domain.exception;


import lombok.Getter;

/**
 * This exception is thrown when a requested entity is not found.
 */
@Getter
public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Class<?> entityClass;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Class<?> clazz, long id) {
        super(String.format("Entity %s with id %d not found", clazz.getSimpleName(), id));
        this.entityClass = clazz;

    }

    public EntityNotFoundException(Class<?> clazz, String id) {
        super(String.format("Entity %s with id %s already exists", clazz.getSimpleName(), id));
    }

}