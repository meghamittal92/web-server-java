package com.client.calorieserver.domain.mapper;

import com.client.calorieserver.domain.dto.CalorieView;
import com.client.calorieserver.domain.dto.CreateCalorieRequest;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.db.UserDay;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.CaloriePerDayRepository;
import com.client.calorieserver.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CalorieMapper {

    private UserRepository userRepository;


    private CaloriePerDayRepository caloriePerDayRepository;

    @Autowired
    public void setUserRepository
            (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCaloriePerDayRepository(CaloriePerDayRepository caloriePerDayRepository) {
        this.caloriePerDayRepository = caloriePerDayRepository;
    }

    @Mapping(target = "withinLimit", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    public abstract Calorie toCalorie(CreateCalorieRequest createCalorieRequest);

    public abstract CalorieView toCalorieView(Calorie calorie);

    public abstract List<CalorieView> toCalorieView(List<Calorie> calorie);

    @Mapping(source = "userDTO.id", target = "userId")
    public abstract Calorie toCalorie(CalorieDTO calorieDTO);

    public abstract List<Calorie> toCalorie(List<CalorieDTO> calorieDTOs);

    @Mapping(source = "userId", target = "userDTO", qualifiedByName = "userIdToUserDTO")
    public abstract CalorieDTO toCalorieDTO(Calorie calorie);


    @Named("userIdToUserDTO")
    public UserDTO userIdToUserDTO(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, userId)
        );

    }

    @AfterMapping
    public void afterMappingCalorieDTO(Calorie calorie, @MappingTarget CalorieDTO calorieDTO) {
        final UserDay userDay = new UserDay(calorieDTO.getUserDTO().getId(), calorie.getDateTime().toLocalDate());
        calorieDTO.setCaloriePerDayDTO(caloriePerDayRepository.findById(userDay).orElseThrow(
                () -> new EntityNotFoundException(Calorie.class, "something")));
    }

    @AfterMapping
    public void afterMappingCalorie(CalorieDTO calorieDTO, @MappingTarget Calorie calorie) {
        calorie.setTotalCaloriesForDay(calorieDTO.getCaloriePerDayDTO().getTotalCalories());
        if (calorie.getTotalCaloriesForDay() <= calorieDTO.getUserDTO().getExpectedCaloriesPerDay())
            calorie.setWithinLimit(true);
        else
            calorie.setWithinLimit(false);
    }


}
