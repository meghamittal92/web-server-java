package com.client.calorieserver.repository;

import com.client.calorieserver.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for User objects.
 */
public interface UserRepository extends CrudRepository<User, Long> {


    public Optional<User> findByUsername(@Param("username") String username);

    public boolean existsByUsername(@Param("username") String username);
}