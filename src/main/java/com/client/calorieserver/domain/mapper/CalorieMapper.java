package com.client.calorieserver.domain.mapper;

import com.client.calorieserver.accessor.CalorieAccessor;
import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.request.CreateCalorieRequest;
import com.client.calorieserver.domain.dto.request.CreateUserCalorieRequest;
import com.client.calorieserver.domain.dto.request.UpdateCalorieRequest;
import com.client.calorieserver.domain.dto.response.AdminCalorieView;
import com.client.calorieserver.domain.dto.response.UserCalorieView;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CalorieMapper {

	private UserRepository userRepository;

	private CalorieAccessor calorieAccessor;

	@Autowired
	public void setCalorieAccessor(final CalorieAccessor calorieAccessor) {
		this.calorieAccessor = calorieAccessor;
	}

	@Autowired
	public void setUserRepository(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Mapping(target = "withinLimit", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "totalCaloriesForDay", ignore = true)
	@Mapping(source = "userId", target = "userId")
	public abstract Calorie toCalorie(CreateUserCalorieRequest createUserCalorieRequest, final Long userId);

	@Mapping(target = "withinLimit", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "totalCaloriesForDay", ignore = true)
	public abstract Calorie toCalorie(CreateCalorieRequest createCalorieRequest);

	@Mapping(target = "withinLimit", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "totalCaloriesForDay", ignore = true)
	@Mapping(target = "userId", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Calorie updateCalorie(UpdateCalorieRequest updateCalorieRequest,
			@MappingTarget Calorie originalCalorie);

	public abstract UserCalorieView toUserCalorieView(Calorie calorie);

	public abstract AdminCalorieView toAdminCalorieView(Calorie calorie);

	public abstract List<UserCalorieView> toUserCalorieView(List<Calorie> calorie);

	public abstract List<AdminCalorieView> toAdminCalorieView(List<Calorie> calorie);

	@Mapping(source = "userDTO.id", target = "userId")
	@Mapping(target = "totalCaloriesForDay", ignore = true)
	@Mapping(target = "withinLimit", ignore = true)
	public abstract Calorie toCalorie(CalorieDTO calorieDTO);

	public abstract List<Calorie> toCalorie(List<CalorieDTO> calorieDTOs);

	@Mapping(source = "userId", target = "userDTO", qualifiedByName = "userIdToUserDTO")
	@Mapping(target = "caloriePerDayDTO", ignore = true)
	public abstract CalorieDTO toCalorieDTO(Calorie calorie);

	@Mapping(target = "caloriePerDayDTO", ignore = true)
	@Mapping(source = "userId", target = "userDTO", qualifiedByName = "userIdToUserDTO")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract CalorieDTO updateCalorieDTO(Calorie calorie, @MappingTarget CalorieDTO originalCalorieDTO);

	@Named("userIdToUserDTO")
	public UserDTO userIdToUserDTO(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, userId));

	}

	@AfterMapping
	public void afterMappingCalorie(CalorieDTO calorieDTO, @MappingTarget Calorie calorie) {

		calorie.setTotalCaloriesForDay(calorieDTO.getCaloriePerDayDTO().getTotalCalories());
		if (calorie.getTotalCaloriesForDay() <= calorieDTO.getUserDTO().getExpectedCaloriesPerDay())
			calorie.setWithinLimit(true);
		else
			calorie.setWithinLimit(false);
	}

	@AfterMapping
	public void afterMappingCalorie(final CreateCalorieRequest createCalorieRequest, @MappingTarget Calorie calorie) {
		if (calorie.getNumCalories() == null) {
			calorie.setNumCalories(calorieAccessor.getCalories(calorie.getMealDetails(), calorie.getUserId()));
		}

	}

	@AfterMapping
	public void afterMappingCalorie(final CreateUserCalorieRequest createUserCalorieRequest,
			@MappingTarget Calorie calorie) {
		if (calorie.getNumCalories() == null) {
			calorie.setNumCalories(calorieAccessor.getCalories(calorie.getMealDetails(), calorie.getUserId()));
		}

	}

}
