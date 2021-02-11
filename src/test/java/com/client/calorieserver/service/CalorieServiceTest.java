package com.client.calorieserver.service;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.CaloriePerDayDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.db.UserDay;
import com.client.calorieserver.domain.exception.EntityAlreadyExistsException;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.CaloriePerDayRepository;
import com.client.calorieserver.repository.CalorieRepository;
import com.client.calorieserver.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CalorieServiceTest {
    private CalorieService calorieService;
    @Mock
    CalorieRepository calorieRepository;
    @Mock
    CalorieMapper calorieMapper;
    @Mock
    CaloriePerDayRepository caloriePerDayRepository;

    private static final Long default_calorie_id = 1L;
    private static final Long default_calorie_user_id = 1L;
    private static final LocalDateTime default_date_time = LocalDateTime.now();
    private static final String default_calorie_detail = "first_meal";
    private static final Integer default_calorie_value = 50;
    private static final Integer default_calorie_per_day = 100;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        MockitoAnnotations.openMocks(this);
        calorieService = new CalorieService(calorieRepository,
                calorieMapper, caloriePerDayRepository);
    }

    @Test
    void findOneByUser(){
        Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong())).
                thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            calorieService.findOneByUser(default_calorie_user_id, default_calorie_id);
        });
        CalorieDTO calorieDTO = getDefaultDTO();
        Mockito.when(calorieRepository.findByUserIdAndId(Mockito.any(), Mockito.any())).
                thenReturn(Optional.of(calorieDTO));
        Calorie calorie = getDefaultCalorie();
        Mockito.when(calorieMapper.toCalorie(Mockito.any(CalorieDTO.class))).thenReturn(calorie);
        Calorie userCalorie = calorieService.findOneByUser(default_calorie_user_id, default_calorie_id);
        assert userCalorie.getUserId().equals(default_calorie_user_id);
        assert userCalorie.getId().equals(default_calorie_id);
    }

    @ParameterizedTest
    @MethodSource("createCalorieProvider")
    void createCalorie(Calorie calorie, CalorieDTO calorieDTO, Optional<CaloriePerDayDTO> caloriePerDayDTO){
        ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor.forClass(CaloriePerDayDTO.class);

        Mockito.when(calorieRepository.save(Mockito.any(CalorieDTO.class))).thenReturn(calorieDTO);
        Mockito.when(calorieMapper.toCalorie(Mockito.any(CalorieDTO.class))).thenReturn(calorie);
        Mockito.when(caloriePerDayRepository.findById(Mockito.any(UserDay.class))).thenReturn(caloriePerDayDTO);
        calorieService.createCalorie(calorie);

        Mockito.verify(caloriePerDayRepository).save(caloriePerDayDTOArgumentCaptor.capture());
        Integer oldCalorie = 0;
        if (caloriePerDayDTO.isPresent()){
            oldCalorie = default_calorie_per_day;
        }
        assert caloriePerDayDTOArgumentCaptor.getValue().getTotalCalories() == (oldCalorie+default_calorie_value);
    }

    @Test
    void deleteById(){
        Mockito.when(calorieRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            calorieService.deleteById(default_calorie_id);
        });
        Optional<CaloriePerDayDTO> caloriePerDayDTOOptional = Optional.of(getDefaultCaloriePerDay());
        Mockito.when(calorieRepository.findById(Mockito.any())).thenReturn(Optional.of(getDefaultDTO()));
        Mockito.when(caloriePerDayRepository.findById(Mockito.any())).thenReturn(caloriePerDayDTOOptional);
        ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor.forClass(CaloriePerDayDTO.class);

        calorieService.deleteById(default_calorie_id);
        Mockito.verify(caloriePerDayRepository).save(caloriePerDayDTOArgumentCaptor.capture());
        assert caloriePerDayDTOArgumentCaptor.getValue().getTotalCalories() == (default_calorie_per_day-default_calorie_value);
    }

    @Test
    void deleteUserCalorie(){
        Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            calorieService.deleteById(default_calorie_user_id, default_calorie_id);
        });
        Optional<CaloriePerDayDTO> caloriePerDayDTOOptional = Optional.of(getDefaultCaloriePerDay());
        Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(getDefaultDTO()));
        Mockito.when(caloriePerDayRepository.findById(Mockito.any())).thenReturn(caloriePerDayDTOOptional);
        ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor.forClass(CaloriePerDayDTO.class);

        calorieService.deleteById(default_calorie_user_id, default_calorie_id);
        Mockito.verify(caloriePerDayRepository).save(caloriePerDayDTOArgumentCaptor.capture());
        assert caloriePerDayDTOArgumentCaptor.getValue().getTotalCalories() == (default_calorie_per_day-default_calorie_value);
    }

    @Test
    void findById(){
        Mockito.when(calorieRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            calorieService.findById( default_calorie_id);
        });

        Mockito.when(calorieRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getDefaultDTO()));
        Mockito.when(calorieMapper.toCalorie(Mockito.any(CalorieDTO.class))).thenReturn(getDefaultCalorie());
        Calorie calorie = calorieService.findById(default_calorie_id);
        assert calorie.getId().equals(getDefaultCalorie().getId());
        assert calorie.getNumCalories().equals(getDefaultCalorie().getNumCalories());
    }

    @Test
    void replaceById(){
        Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            calorieService.replaceById( default_calorie_user_id, default_calorie_id, getDefaultCalorie());
        });

        Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(getDefaultDTO()));
        CaloriePerDayDTO caloriePerDayDTO = getDefaultCaloriePerDay();
        Mockito.when(caloriePerDayRepository.findById(Mockito.any(UserDay.class))).thenReturn(Optional.of(caloriePerDayDTO));
        CalorieDTO updatedCalorieDTO = getDefaultDTO();
        updatedCalorieDTO.setNumCalories(2*default_calorie_value);

        Mockito.when(calorieMapper.toCalorieDTO(Mockito.any(Calorie.class))).thenReturn(updatedCalorieDTO);
        ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor.forClass(CaloriePerDayDTO.class);

        calorieService.replaceById(default_calorie_user_id, default_calorie_id, getDefaultCalorie());
        Mockito.verify(caloriePerDayRepository, Mockito.times(2)).save(caloriePerDayDTOArgumentCaptor.capture());
        List<CaloriePerDayDTO> caloriePerDayDTOS = caloriePerDayDTOArgumentCaptor.getAllValues();
        assert caloriePerDayDTOS.get(1).getTotalCalories() == default_calorie_per_day+default_calorie_value;
    }

    @Test
    void updateByUserCalorieId(){
        Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            calorieService.updateById( default_calorie_user_id, default_calorie_id, getDefaultCalorie());
        });

        Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(getDefaultDTO()));
        CaloriePerDayDTO caloriePerDayDTO = getDefaultCaloriePerDay();
        Mockito.when(caloriePerDayRepository.findById(Mockito.any(UserDay.class))).thenReturn(Optional.of(caloriePerDayDTO));
        CalorieDTO updatedCalorieDTO = getDefaultDTO();
        updatedCalorieDTO.setNumCalories(2*default_calorie_value);

        Mockito.when(calorieMapper.updateCalorieDTO(Mockito.any(), Mockito.any())).thenReturn(updatedCalorieDTO);
        ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor.forClass(CaloriePerDayDTO.class);

        calorieService.updateById(default_calorie_user_id, default_calorie_id, getDefaultCalorie());
        Mockito.verify(caloriePerDayRepository, Mockito.times(2)).save(caloriePerDayDTOArgumentCaptor.capture());
        List<CaloriePerDayDTO> caloriePerDayDTOS = caloriePerDayDTOArgumentCaptor.getAllValues();
        assert caloriePerDayDTOS.get(1).getTotalCalories() == default_calorie_per_day+default_calorie_value;
    }

    @Test
    void updateByCalorieId(){
        Mockito.when(calorieRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            calorieService.updateById(default_calorie_id, getDefaultCalorie());
        });

        Mockito.when(calorieRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(getDefaultDTO()));
        CaloriePerDayDTO caloriePerDayDTO = getDefaultCaloriePerDay();
        Mockito.when(caloriePerDayRepository.findById(Mockito.any(UserDay.class))).thenReturn(Optional.of(caloriePerDayDTO));
        CalorieDTO updatedCalorieDTO = getDefaultDTO();
        updatedCalorieDTO.setNumCalories(2*default_calorie_value);

        Mockito.when(calorieMapper.updateCalorieDTO(Mockito.any(), Mockito.any())).thenReturn(updatedCalorieDTO);
        ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor.forClass(CaloriePerDayDTO.class);

        calorieService.updateById(default_calorie_id, getDefaultCalorie());
        Mockito.verify(caloriePerDayRepository, Mockito.times(2)).save(caloriePerDayDTOArgumentCaptor.capture());
        List<CaloriePerDayDTO> caloriePerDayDTOS = caloriePerDayDTOArgumentCaptor.getAllValues();
        assert caloriePerDayDTOS.get(1).getTotalCalories() == default_calorie_per_day+default_calorie_value;
    }

    private static Stream<Arguments> createCalorieProvider() {
        Calorie calorieOne = getDefaultCalorie();
        CalorieDTO calorieDTO = getDefaultDTO();
        Optional<CaloriePerDayDTO> emptyCaloriePerDayDTO = Optional.empty();
        Optional<CaloriePerDayDTO> caloriePerDayDTO = Optional.of(getDefaultCaloriePerDay());
        return Stream.of(
                Arguments.of(calorieOne, calorieDTO, emptyCaloriePerDayDTO),
                Arguments.of(calorieOne, calorieDTO, caloriePerDayDTO)
        );
    }

    private static CalorieDTO getDefaultDTO(){
        CalorieDTO calorieDTO = new CalorieDTO();
        calorieDTO.setId(default_calorie_id);
        calorieDTO.setUserId(default_calorie_user_id);
        calorieDTO.setDateTime(default_date_time);
        calorieDTO.setMealDetails(default_calorie_detail);
        calorieDTO.setNumCalories(default_calorie_value);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(default_calorie_user_id);
        calorieDTO.setUserDTO(userDTO);
        return calorieDTO;
    }

    private static Calorie getDefaultCalorie(){
        Calorie calorie = new Calorie();
        calorie.setId(default_calorie_id);
        calorie.setUserId(default_calorie_user_id);
        calorie.setDateTime(default_date_time);
        calorie.setMealDetails(default_calorie_detail);
        calorie.setNumCalories(default_calorie_value);
        return calorie;
    }

    private static CaloriePerDayDTO getDefaultCaloriePerDay(){
        CaloriePerDayDTO caloriePerDayDTO = new CaloriePerDayDTO();
        caloriePerDayDTO.setTotalCalories(default_calorie_per_day);
        UserDay userDay = new UserDay();
        userDay.setUserId(default_calorie_user_id);
        userDay.setDate(default_date_time.toLocalDate());
        caloriePerDayDTO.setId(userDay);
        return caloriePerDayDTO;
    }
}
