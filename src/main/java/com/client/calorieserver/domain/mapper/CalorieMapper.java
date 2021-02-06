package com.client.calorieserver.domain.mapper;

import com.client.calorieserver.domain.dto.CalorieView;
import com.client.calorieserver.domain.dto.CreateCalorieRequest;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.UserRepository;
import com.client.calorieserver.service.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CalorieMapper {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository
            (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public abstract CalorieView toCalorieView(Calorie calorie);

    public abstract List<CalorieView> toCalorieView(List<Calorie> calorie);

    @Mapping(source = "userDTO", target = "userId", qualifiedByName = "userDTOToUserId")
    public abstract Calorie toCalorie(CalorieDTO calorieDTO);

    public abstract List<Calorie> toCalorie(List<CalorieDTO> calorieDTOs);

    public abstract Calorie toCalorie(CreateCalorieRequest createCalorieRequest);


    @Named("userDTOToUserId")
    public Long userDTOToUserId(UserDTO userDTO) {
        return userDTO.getId();

    }

    @Named("userIdToUserDTO")
    public UserDTO userIdToUserDTO(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, userId)
        );

    }

    @Mapping(source = "userId", target = "userDTO", qualifiedByName = "userIdToUserDTO")
    public abstract CalorieDTO toCalorieDTO(Calorie calorie);
}
