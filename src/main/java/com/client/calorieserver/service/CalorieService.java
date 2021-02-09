package com.client.calorieserver.service;

import com.client.calorieserver.domain.dto.CalorieView;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.CaloriePerDayDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.db.UserDay;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.repository.CaloriePerDayRepository;
import com.client.calorieserver.repository.CalorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalorieService {

    private final CalorieRepository calorieRepository;
    private final CalorieMapper calorieMapper;
    private final CaloriePerDayRepository caloriePerDayRepository;

    public Page<Calorie> findAllByUser(final Long userId, final Pageable pageable) {

        final Page<CalorieDTO> calorieDTOS = calorieRepository.findAllByUser(userId, pageable);

        return calorieDTOS.map(calorieMapper::toCalorie);

    }

    public Calorie findOneByUser(final Long userId, final Long calorieId) {

        final CalorieDTO calorieDTO = calorieRepository.findOneByUser(userId, calorieId).orElseThrow(
                () -> new EntityNotFoundException(Calorie.class, calorieId)
        );
        ;
        return calorieMapper.toCalorie(calorieDTO);

    }

    @Transactional
    public Calorie createCalorieForUser(final Long userId, final Calorie calorie) {


        calorie.setUserId(userId);

        saveOrUpdateCaloriesPerDay(calorie.getUserId(), calorie.getDateTime(), calorie.getNumCalories());
        final CalorieDTO calorieDTO = calorieRepository.save(calorieMapper.toCalorieDTO(calorie));


        return calorieMapper.toCalorie(calorieDTO);


    }


    @Transactional
    public Calorie updateById(final Long userId, final Long calorieId, final Calorie updatedCalorie) {

        updatedCalorie.setUserId(userId);
        final CalorieDTO originalCalorieDTO = calorieRepository.findOneByUser(userId, calorieId).orElseThrow(
                () -> new EntityNotFoundException(Calorie.class, calorieId)
        );

        CalorieDTO updatedCalorieDTO = calorieMapper.updateCalorieDTO(updatedCalorie, originalCalorieDTO);

        if (originalCalorieDTO.getNumCalories() != updatedCalorieDTO.getNumCalories()) {
            saveOrUpdateCaloriesPerDay(updatedCalorie.getUserId(), updatedCalorie.getDateTime(), updatedCalorieDTO.getNumCalories() - originalCalorieDTO.getNumCalories());
        }

        return calorieMapper.toCalorie(calorieRepository.save(updatedCalorieDTO));
    }

    @Transactional
    public void deleteById(final Long userId, final Long calorieId) {

        CalorieDTO calorieDTO = calorieRepository.findOneByUser(userId, calorieId).orElseThrow(
                () -> new EntityNotFoundException(Calorie.class, calorieId)
        );

        saveOrUpdateCaloriesPerDay(calorieDTO.getUserDTO().getId(), calorieDTO.getDateTime(), -1 * calorieDTO.getNumCalories());
        calorieRepository.deleteOneByUserId(userId, calorieId);
    }

    private void saveOrUpdateCaloriesPerDay(final Long userId, final LocalDateTime dateTime, final int deltaCalories) {
        final UserDay userDay = new UserDay(userId, dateTime.toLocalDate());
        Optional<CaloriePerDayDTO> caloriePerDayDTOOptional = caloriePerDayRepository.findById(userDay);
        CaloriePerDayDTO caloriePerDayDTO;
        if (caloriePerDayDTOOptional.isPresent()) {
            caloriePerDayDTO = caloriePerDayDTOOptional.get();

            caloriePerDayDTO.setTotalCalories(caloriePerDayDTO.getTotalCalories() + deltaCalories);


        } else {
            caloriePerDayDTO = new CaloriePerDayDTO();
            caloriePerDayDTO.setId(userDay);
            caloriePerDayDTO.setTotalCalories(deltaCalories);

        }

        caloriePerDayRepository.save(caloriePerDayDTO);
    }

    public Page<Calorie> findAll(Specification<CalorieDTO> spec, Pageable pageable) {

        final Page<CalorieDTO> calorieDTOS = calorieRepository.findAll(spec, pageable);

        return calorieDTOS.map(calorieMapper::toCalorie);
    }
}
