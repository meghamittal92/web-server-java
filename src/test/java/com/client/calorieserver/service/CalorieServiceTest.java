package com.client.calorieserver.service;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.CaloriePerDayDTO;
import com.client.calorieserver.domain.dto.db.UserDay;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.mapper.CalorieMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.repository.CaloriePerDayRepository;
import com.client.calorieserver.repository.CalorieRepository;
import com.client.calorieserver.utils.TestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
		MockitoAnnotations.openMocks(this);
		calorieService = new CalorieService(calorieRepository, calorieMapper, caloriePerDayRepository);
	}

	@Test
	void findOneByUser() {
		Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(Optional.empty());

		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			calorieService.findOneByUser(TestData.default_calorie_user_id, TestData.default_calorie_id);
		});
		CalorieDTO calorieDTO = TestData.getDefaultCalorieDTO();
		Mockito.when(calorieRepository.findByUserIdAndId(Mockito.any(), Mockito.any()))
				.thenReturn(Optional.of(calorieDTO));
		Calorie calorie = TestData.getDefaultCalorie();
		Mockito.when(calorieMapper.toCalorie(Mockito.any(CalorieDTO.class))).thenReturn(calorie);
		Calorie userCalorie = calorieService.findOneByUser(TestData.default_calorie_user_id,
				TestData.default_calorie_id);
		assert userCalorie.getUserId().equals(TestData.default_calorie_user_id);
		assert userCalorie.getId().equals(TestData.default_calorie_id);
	}

	@ParameterizedTest
	@MethodSource("createCalorieProvider")
	void createCalorie(Calorie calorie, CalorieDTO calorieDTO, Optional<CaloriePerDayDTO> caloriePerDayDTO) {
		ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor
				.forClass(CaloriePerDayDTO.class);

		Mockito.when(calorieRepository.save(Mockito.any(CalorieDTO.class))).thenReturn(calorieDTO);
		Mockito.when(calorieMapper.toCalorie(Mockito.any(CalorieDTO.class))).thenReturn(calorie);
		Mockito.when(caloriePerDayRepository.findById(Mockito.any(UserDay.class))).thenReturn(caloriePerDayDTO);
		calorieService.createCalorie(calorie);

		Mockito.verify(caloriePerDayRepository).save(caloriePerDayDTOArgumentCaptor.capture());
		Integer oldCalorie = 0;
		if (caloriePerDayDTO.isPresent()) {
			oldCalorie = TestData.default_calorie_per_day;
		}
		assert caloriePerDayDTOArgumentCaptor.getValue()
				.getTotalCalories() == (oldCalorie + TestData.default_calorie_value);
	}

	@Test
	void deleteById() {
		Mockito.when(calorieRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			calorieService.deleteById(TestData.default_calorie_id);
		});
		Optional<CaloriePerDayDTO> caloriePerDayDTOOptional = Optional.of(TestData.getDefaultCaloriePerDayDTO());
		Mockito.when(calorieRepository.findById(Mockito.any()))
				.thenReturn(Optional.of(TestData.getDefaultCalorieDTO()));
		Mockito.when(caloriePerDayRepository.findById(Mockito.any())).thenReturn(caloriePerDayDTOOptional);
		ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor
				.forClass(CaloriePerDayDTO.class);

		calorieService.deleteById(TestData.default_calorie_id);
		Mockito.verify(caloriePerDayRepository).save(caloriePerDayDTOArgumentCaptor.capture());
		assert caloriePerDayDTOArgumentCaptor.getValue()
				.getTotalCalories() == (TestData.default_calorie_per_day - TestData.default_calorie_value);
	}

	@Test
	void deleteUserCalorie() {
		Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(Optional.empty());
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			calorieService.deleteById(TestData.default_calorie_user_id, TestData.default_calorie_id);
		});
		Optional<CaloriePerDayDTO> caloriePerDayDTOOptional = Optional.of(TestData.getDefaultCaloriePerDayDTO());
		Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(Optional.of(TestData.getDefaultCalorieDTO()));
		Mockito.when(caloriePerDayRepository.findById(Mockito.any())).thenReturn(caloriePerDayDTOOptional);
		ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor
				.forClass(CaloriePerDayDTO.class);

		calorieService.deleteById(TestData.default_calorie_user_id, TestData.default_calorie_id);
		Mockito.verify(caloriePerDayRepository).save(caloriePerDayDTOArgumentCaptor.capture());
		assert caloriePerDayDTOArgumentCaptor.getValue()
				.getTotalCalories() == (TestData.default_calorie_per_day - TestData.default_calorie_value);
	}

	@Test
	void findById() {
		Mockito.when(calorieRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			calorieService.findById(TestData.default_calorie_id);
		});

		Mockito.when(calorieRepository.findById(Mockito.anyLong()))
				.thenReturn(Optional.of(TestData.getDefaultCalorieDTO()));
		Mockito.when(calorieMapper.toCalorie(Mockito.any(CalorieDTO.class))).thenReturn(TestData.getDefaultCalorie());
		Calorie calorie = calorieService.findById(TestData.default_calorie_id);
		assert calorie.getId().equals(TestData.getDefaultCalorie().getId());
		assert calorie.getNumCalories().equals(TestData.getDefaultCalorie().getNumCalories());
	}

	@Test
	void replaceById() {
		Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(Optional.empty());
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			calorieService.replaceById(TestData.default_calorie_user_id, TestData.default_calorie_id,
					TestData.getDefaultCalorie());
		});

		Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(Optional.of(TestData.getDefaultCalorieDTO()));
		CaloriePerDayDTO caloriePerDayDTO = TestData.getDefaultCaloriePerDayDTO();
		Mockito.when(caloriePerDayRepository.findById(Mockito.any(UserDay.class)))
				.thenReturn(Optional.of(caloriePerDayDTO));
		CalorieDTO updatedCalorieDTO = TestData.getDefaultCalorieDTO();
		updatedCalorieDTO.setNumCalories(2 * TestData.default_calorie_value);

		Mockito.when(calorieMapper.toCalorieDTO(Mockito.any(Calorie.class))).thenReturn(updatedCalorieDTO);
		ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor
				.forClass(CaloriePerDayDTO.class);

		calorieService.replaceById(TestData.default_calorie_user_id, TestData.default_calorie_id,
				TestData.getDefaultCalorie());
		Mockito.verify(caloriePerDayRepository, Mockito.times(2)).save(caloriePerDayDTOArgumentCaptor.capture());
		List<CaloriePerDayDTO> caloriePerDayDTOS = caloriePerDayDTOArgumentCaptor.getAllValues();
		assert caloriePerDayDTOS.get(1).getTotalCalories() == TestData.default_calorie_per_day
				+ TestData.default_calorie_value;
	}

	@Test
	void updateByUserCalorieId() {
		Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(Optional.empty());
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			calorieService.updateById(TestData.default_calorie_user_id, TestData.default_calorie_id,
					TestData.getDefaultCalorie());
		});

		Mockito.when(calorieRepository.findByUserIdAndId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(Optional.of(TestData.getDefaultCalorieDTO()));
		CaloriePerDayDTO caloriePerDayDTO = TestData.getDefaultCaloriePerDayDTO();
		Mockito.when(caloriePerDayRepository.findById(Mockito.any(UserDay.class)))
				.thenReturn(Optional.of(caloriePerDayDTO));
		CalorieDTO updatedCalorieDTO = TestData.getDefaultCalorieDTO();
		updatedCalorieDTO.setNumCalories(2 * TestData.default_calorie_value);

		Mockito.when(calorieMapper.updateCalorieDTO(Mockito.any(), Mockito.any())).thenReturn(updatedCalorieDTO);
		ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor
				.forClass(CaloriePerDayDTO.class);

		calorieService.updateById(TestData.default_calorie_user_id, TestData.default_calorie_id,
				TestData.getDefaultCalorie());
		Mockito.verify(caloriePerDayRepository, Mockito.times(2)).save(caloriePerDayDTOArgumentCaptor.capture());
		List<CaloriePerDayDTO> caloriePerDayDTOS = caloriePerDayDTOArgumentCaptor.getAllValues();
		assert caloriePerDayDTOS.get(1).getTotalCalories() == TestData.default_calorie_per_day
				+ TestData.default_calorie_value;
	}

	@Test
	void updateByCalorieId() {
		Mockito.when(calorieRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			calorieService.updateById(TestData.default_calorie_id, TestData.getDefaultCalorie());
		});

		Mockito.when(calorieRepository.findById(Mockito.anyLong()))
				.thenReturn(Optional.of(TestData.getDefaultCalorieDTO()));
		CaloriePerDayDTO caloriePerDayDTO = TestData.getDefaultCaloriePerDayDTO();
		Mockito.when(caloriePerDayRepository.findById(Mockito.any(UserDay.class)))
				.thenReturn(Optional.of(caloriePerDayDTO));
		CalorieDTO updatedCalorieDTO = TestData.getDefaultCalorieDTO();
		updatedCalorieDTO.setNumCalories(2 * TestData.default_calorie_value);

		Mockito.when(calorieMapper.updateCalorieDTO(Mockito.any(), Mockito.any())).thenReturn(updatedCalorieDTO);
		ArgumentCaptor<CaloriePerDayDTO> caloriePerDayDTOArgumentCaptor = ArgumentCaptor
				.forClass(CaloriePerDayDTO.class);

		calorieService.updateById(TestData.default_calorie_id, TestData.getDefaultCalorie());
		Mockito.verify(caloriePerDayRepository, Mockito.times(2)).save(caloriePerDayDTOArgumentCaptor.capture());
		List<CaloriePerDayDTO> caloriePerDayDTOS = caloriePerDayDTOArgumentCaptor.getAllValues();
		assert caloriePerDayDTOS.get(1).getTotalCalories() == TestData.default_calorie_per_day
				+ TestData.default_calorie_value;
	}

	private static Stream<Arguments> createCalorieProvider() {
		Calorie calorieOne = TestData.getDefaultCalorie();
		CalorieDTO calorieDTO = TestData.getDefaultCalorieDTO();
		Optional<CaloriePerDayDTO> emptyCaloriePerDayDTO = Optional.empty();
		Optional<CaloriePerDayDTO> caloriePerDayDTO = Optional.of(TestData.getDefaultCaloriePerDayDTO());
		return Stream.of(Arguments.of(calorieOne, calorieDTO, emptyCaloriePerDayDTO),
				Arguments.of(calorieOne, calorieDTO, caloriePerDayDTO));
	}

}