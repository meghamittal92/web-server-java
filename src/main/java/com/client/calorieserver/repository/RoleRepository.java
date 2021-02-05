package com.client.calorieserver.repository;

import com.client.calorieserver.domain.dto.db.RoleDTO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<RoleDTO, Long> {

    public Optional<RoleDTO> findByName(@Param("name") String roleName);
}
