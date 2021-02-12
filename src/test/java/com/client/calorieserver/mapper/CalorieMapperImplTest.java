package com.client.calorieserver.mapper;

import com.client.calorieserver.accessor.CalorieAccessor;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.request.CreateCalorieRequest;
import com.client.calorieserver.domain.dto.request.CreateUserCalorieRequest;
import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CalorieMapperImplTest {
    private static final int default_calories = 20;
    private static final LocalDateTime calorie_time = LocalDateTime.now()
            .truncatedTo(ChronoUnit.SECONDS);
    private static final Long default_calorie_id = 1L;
    private CalorieMapper calorieMapper;

    @Mock
    CalorieAccessor calorieAccessor;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        calorieMapper = Mockito.mock(CalorieMapper.class, Mockito.CALLS_REAL_METHODS);
        calorieMapper.setCalorieAccessor(calorieAccessor);
    }

    @Test
    void afterMappingCalorie(){
        CalorieDTO calorieDTO = TestData.getDefaultCalorieDTO();
        calorieDTO.getUserDTO().setExpectedCaloriesPerDay(
                TestData.getDefaultCaloriePerDayDTO().getTotalCalories());
        calorieDTO.setCaloriePerDayDTO(TestData.getDefaultCaloriePerDayDTO());
        Calorie calorie = TestData.getDefaultCalorie();
        calorie.setTotalCaloriesForDay(2*calorieDTO.getNumCalories());
        calorieMapper.afterMappingCalorie(calorieDTO, calorie);
        assert calorie.getNumCalories().equals(calorieDTO.getNumCalories());
        assert calorie.isWithinLimit();

        calorieDTO = TestData.getDefaultCalorieDTO();
        calorieDTO.getUserDTO().setExpectedCaloriesPerDay(
                calorieDTO.getNumCalories()/2);
        calorieDTO.setCaloriePerDayDTO(TestData.getDefaultCaloriePerDayDTO());
        calorie = TestData.getDefaultCalorie();
        calorie.setTotalCaloriesForDay(2*calorieDTO.getNumCalories());
        calorieMapper.afterMappingCalorie(calorieDTO, calorie);
        assert calorie.getNumCalories().equals(calorieDTO.getNumCalories());
        assert !calorie.isWithinLimit();
    }

    @Test
    void afterMappingCalorieCreateCalorieRequest(){
        CreateCalorieRequest createCalorieRequest = new CreateCalorieRequest();
        Calorie calorie = TestData.getDefaultCalorie();
        calorie.setNumCalories(null);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(calorieAccessor.getCalories(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(100);
        calorieMapper.afterMappingCalorie(createCalorieRequest, calorie);
        Mockito.verify(calorieAccessor).getCalories(Mockito.anyString(), argumentCaptor.capture());
        assert argumentCaptor.getValue().equals(calorie.getUserId());
        assert calorie.getNumCalories() == 100;
        calorieMapper.afterMappingCalorie(createCalorieRequest, calorie);
        calorie.setNumCalories(100);
    }

    @Test
    void afterMappingCalorieUpdateCalorieRequest(){
        CreateUserCalorieRequest userCalorieRequest = new CreateUserCalorieRequest();
        Calorie calorie = TestData.getDefaultCalorie();
        calorie.setNumCalories(null);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(calorieAccessor.getCalories(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(100);
        calorieMapper.afterMappingCalorie(userCalorieRequest, calorie);
        Mockito.verify(calorieAccessor).getCalories(Mockito.anyString(), argumentCaptor.capture());
        assert argumentCaptor.getValue().equals(calorie.getUserId());
        assert calorie.getNumCalories() == 100;
        calorieMapper.afterMappingCalorie(userCalorieRequest, calorie);
        calorie.setNumCalories(100);
    }

}
