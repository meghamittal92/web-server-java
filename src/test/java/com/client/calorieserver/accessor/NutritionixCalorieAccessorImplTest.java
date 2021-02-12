package com.client.calorieserver.accessor;

import com.client.calorieserver.domain.exception.ExternalCalorieServiceException;
import com.client.calorieserver.domain.model.calorie.accessor.NutritionixErrorResponse;
import com.client.calorieserver.domain.model.calorie.accessor.NutritionixResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class NutritionixCalorieAccessorImplTest {

	private NutritionixCalorieAccessorImpl accessor;

	private final String apiKey = "api_key";

	private final String appId = "app_id";

	private final String MEAL_DETAILS = "burger";

	private final Long TEST_ID = 0L;

	private final String ERROR_MESSAGE = "error";

	private final String ERROR_MESSAGE_ID = "errorMessageID";

	private static final Integer RETURNED_CALORIES = 4;

	@Mock
	Client mockClient;

	@Mock
	WebTarget mockWebTarget;

	@Mock
	Invocation.Builder invocationBuilderMock;

	@Mock
	Response responseMock;

	@Mock
	NutritionixResponse nutritionixResponse;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
		accessor = new NutritionixCalorieAccessorImpl(apiKey, appId, mockClient);
	}

	@Test
	void getCaloriesStatusOKEntityNull() {

		NutritionixErrorResponse nutritionixErrorResponse = new NutritionixErrorResponse(ERROR_MESSAGE,
				ERROR_MESSAGE_ID);
		Mockito.when(mockClient.target(Mockito.anyString())).thenReturn(mockWebTarget);
		Mockito.when(mockWebTarget.path(Mockito.anyString())).thenReturn(mockWebTarget);
		Mockito.when(mockWebTarget.request(Mockito.anyString())).thenReturn(invocationBuilderMock);
		Mockito.when(invocationBuilderMock.header(Mockito.anyString(), Mockito.any()))
				.thenReturn(invocationBuilderMock);
		Mockito.when(invocationBuilderMock.post(Mockito.any(Entity.class))).thenReturn(responseMock);

		Mockito.when(responseMock.getStatus()).thenReturn(HttpStatus.OK.value());
		Mockito.when(responseMock.getEntity()).thenReturn(null);
		Mockito.when(responseMock.readEntity(NutritionixErrorResponse.class)).thenReturn(nutritionixErrorResponse);

		Assertions.assertThrows(ExternalCalorieServiceException.class, () -> {
			this.accessor.getCalories(MEAL_DETAILS, TEST_ID);
		}, ERROR_MESSAGE);

	}

	@Test
	void getCaloriesStatusError() {

		final NutritionixErrorResponse nutritionixErrorResponse = new NutritionixErrorResponse(ERROR_MESSAGE,
				ERROR_MESSAGE_ID);
		Mockito.when(mockClient.target(Mockito.anyString())).thenReturn(mockWebTarget);
		Mockito.when(mockWebTarget.path(Mockito.anyString())).thenReturn(mockWebTarget);
		Mockito.when(mockWebTarget.request(Mockito.anyString())).thenReturn(invocationBuilderMock);
		Mockito.when(invocationBuilderMock.header(Mockito.anyString(), Mockito.any()))
				.thenReturn(invocationBuilderMock);
		Mockito.when(invocationBuilderMock.post(Mockito.any(Entity.class))).thenReturn(responseMock);

		Mockito.when(responseMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
		Mockito.when(responseMock.getEntity()).thenReturn(nutritionixErrorResponse);
		Mockito.when(responseMock.readEntity(NutritionixErrorResponse.class)).thenReturn(nutritionixErrorResponse);

		Assertions.assertThrows(ExternalCalorieServiceException.class, () -> {
			this.accessor.getCalories(MEAL_DETAILS, TEST_ID);
		}, ERROR_MESSAGE);

	}

	@Test
	void getCaloriesSuccess() {

		Mockito.when(mockClient.target(Mockito.anyString())).thenReturn(mockWebTarget);
		Mockito.when(mockWebTarget.path(Mockito.anyString())).thenReturn(mockWebTarget);
		Mockito.when(mockWebTarget.request(Mockito.anyString())).thenReturn(invocationBuilderMock);
		Mockito.when(invocationBuilderMock.header(Mockito.anyString(), Mockito.any()))
				.thenReturn(invocationBuilderMock);
		Mockito.when(invocationBuilderMock.post(Mockito.any(Entity.class))).thenReturn(responseMock);

		Mockito.when(responseMock.getStatus()).thenReturn(HttpStatus.OK.value());
		Mockito.when(responseMock.getEntity()).thenReturn(nutritionixResponse);
		Mockito.when(responseMock.readEntity(NutritionixResponse.class)).thenReturn(nutritionixResponse);
		Mockito.when(nutritionixResponse.getCalories()).thenReturn(RETURNED_CALORIES);

		Assertions.assertEquals(this.accessor.getCalories(MEAL_DETAILS, TEST_ID), RETURNED_CALORIES);

	}

}
