package com.client.calorieserver.repository;

import com.client.calorieserver.domain.dto.db.UserDTO;

import com.client.calorieserver.domain.dto.db.UserDTO_;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for User objects.
 */
public interface UserRepository extends CustomRepository<UserDTO, Long> , JpaSpecificationExecutor<UserDTO> {


    public Optional<UserDTO> findByUsername(@Param(UserDTO_.USERNAME) String username);

    public boolean existsByUsername(@Param(UserDTO_.USERNAME) String username);
    public boolean existsByEmail(@Param(UserDTO_.EMAIL) String email);

    public void deleteById(@Param(UserDTO_.ID) Long userId);

    public Optional<UserDTO> findById(@Param(UserDTO_.ID) Long userId);
}