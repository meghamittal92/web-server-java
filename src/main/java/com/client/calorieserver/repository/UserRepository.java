package com.client.calorieserver.repository;

import com.client.calorieserver.domain.dto.db.UserDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for User objects.
 */
public interface UserRepository extends CrudRepository<UserDTO, Long> {


    public Optional<UserDTO> findByUsername(@Param("username") String username);

    public boolean existsByUsername(@Param("username") String username);
}