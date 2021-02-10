package com.client.calorieserver.repository;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface CalorieRepository extends PagingAndSortingRepository<CalorieDTO, Long>, JpaSpecificationExecutor<CalorieDTO> {

    Page<CalorieDTO> findByUserId(@Param("userId") Long userId, final Pageable pageable);


    Optional<CalorieDTO> findByUserIdAndId(@Param("userId") Long userId, @Param("id") Long calorieId);

    @Modifying
    void deleteByUserIdAndId(@Param("userId") Long userId, @Param("id") Long calorieId);


}

