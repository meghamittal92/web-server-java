package com.client.calorieserver.repository;

import com.client.calorieserver.domain.model.Role;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    public Optional<Role> findByName(@Param("name") String roleName);
}
