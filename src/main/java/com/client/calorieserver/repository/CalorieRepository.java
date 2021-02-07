package com.client.calorieserver.repository;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface CalorieRepository extends PagingAndSortingRepository<CalorieDTO, Long> {


    @Query("SELECT c FROM CalorieDTO c WHERE c.userDTO.id = :userId")
    Page<CalorieDTO> findAllByUser(@Param("userId") Long userId, final Pageable pageable);

    @Query("SELECT c FROM CalorieDTO c WHERE c.userDTO.id = :userId AND c.id = :calorieId")
    Optional<CalorieDTO> findOneByUser(@Param("userId") Long userId, @Param("calorieId") Long calorieId);


    @Modifying
    @Query("DELETE FROM CalorieDTO c WHERE c.userDTO.id = :userId AND c.id = :calorieId")
    void deleteOneByUserId(@Param("userId") Long userId, @Param("calorieId") Long calorieId);


}

