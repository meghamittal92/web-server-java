package com.client.calorieserver.repository;

import com.client.calorieserver.domain.dto.db.RoleDTO;

import com.client.calorieserver.domain.dto.db.RoleDTO_;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<RoleDTO, Long> {

	public Optional<RoleDTO> findByName(@Param(RoleDTO_.NAME) String roleName);

}
