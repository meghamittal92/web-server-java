package com.client.calorieserver.service;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.exception.EntityAlreadyExistsException;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.CalorieRepository;
import com.client.calorieserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalorieService {

    private final CalorieRepository calorieRepository;
    private final CalorieMapper calorieMapper;

    public List<Calorie> findAllByUser(final Long userId) {

        final List<CalorieDTO> calorieDTOS = calorieRepository.findAllByUser(userId);

        return calorieMapper.toCalorie(calorieDTOS);

    }

    public Calorie findOneByUser(final Long userId, final Long calorieId) {

        return calorieMapper.toCalorie(calorieRepository.findOneByUser(userId, calorieId));

    }

    @Transactional
    public Calorie createCalorieForUser(final Calorie calorie) {

        final CalorieDTO calorieDTO = calorieMapper.toCalorieDTO(calorie);
        return calorieMapper.toCalorie(calorieRepository.save(calorieDTO));


    }

    public Calorie replaceById(final Long calorieId, final Calorie updatedCalorie) {

        final CalorieDTO originalCalorieDTO = calorieRepository.findById(calorieId).orElseThrow(
                () -> new EntityNotFoundException(CalorieDTO.class, calorieId)
        );

        final CalorieDTO updatedCalorieDTO = calorieMapper.toCalorieDTO(updatedCalorie);
        updatedCalorieDTO.setId(originalCalorieDTO.getId());


        return calorieMapper.toCalorie(calorieRepository.save(updatedCalorieDTO));
    }

    @Transactional
    public void deleteById(final Long userId, final Long calorieId) {

        calorieRepository.deleteOneByUserId(userId, calorieId);
    }
}
