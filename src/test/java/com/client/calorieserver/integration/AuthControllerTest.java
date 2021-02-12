package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.request.RegisterUserRequest;
import com.client.calorieserver.domain.exception.ApiError;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends BaseIntegrationTest {

	private static final String email_suffix = "@gmail.com";

	private static final String ERROR_CODE_KEY = "errorCode";

	private static final String MESSAGE_KEY = "message";

	private static final String USERNAME_KEY = "username";

	private static final String PASSWORD_KEY = "password";

	private static final String TEST_USERNAME = "user1";

	private static final String TEST_USERNAME_2 = "user2";

	private static final String TEST_PASSWORD = "testPasssword1$";

	private static final String TEST_PASSWORD_2 = "testPasssword1$";

	private static final String WRONG_PASSWORD = "wrongPasssword2$";

	private static final String INVALID_USERNAME = "u!^";

	private static final String INVALID_PASSWORD = "u!^";

	@Test
	void createUser() throws Exception {
		// 1. Test failure without setting username and password
		RegisterUserRequest registerUserRequest = new RegisterUserRequest();
		MvcResult result = mockMvc
				.perform(post(publicRequestPath + registerUserEndpoint).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerUserRequest)))
				.andExpect(status().is4xxClientError()).andReturn();
		JSONObject response = new JSONObject(result.getResponse().getContentAsString());
		assert (response.getString(ERROR_CODE_KEY).equalsIgnoreCase(ApiError.INVALID_INPUT.getErrorCode()));
		assert (response.getString(MESSAGE_KEY).equalsIgnoreCase(ApiError.INVALID_INPUT.getErrorMessage()));

		// Test failure with only username
		registerUserRequest.setUsername(TEST_USERNAME);

		result = mockMvc
				.perform(post(publicRequestPath + registerUserEndpoint).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerUserRequest)))
				.andExpect(status().is4xxClientError()).andReturn();
		response = new JSONObject(result.getResponse().getContentAsString());
		assert (response.getString(ERROR_CODE_KEY).equalsIgnoreCase(ApiError.INVALID_INPUT.getErrorCode()));
		assert (response.getString(MESSAGE_KEY).equalsIgnoreCase(ApiError.INVALID_INPUT.getErrorMessage()));

		// Test failure with invalid username
		registerUserRequest.setUsername(INVALID_USERNAME);
		result = mockMvc
				.perform(post(publicRequestPath + registerUserEndpoint).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerUserRequest)))
				.andExpect(status().is4xxClientError()).andReturn();
		response = new JSONObject(result.getResponse().getContentAsString());
		assert (response.getString(ERROR_CODE_KEY).equalsIgnoreCase(ApiError.INVALID_INPUT.getErrorCode()));
		assert (response.getString(MESSAGE_KEY).equalsIgnoreCase(ApiError.INVALID_INPUT.getErrorMessage()));

		// Test failure with invalid password
		registerUserRequest.setUsername(TEST_USERNAME);
		registerUserRequest.setPassword(INVALID_PASSWORD);
		result = mockMvc
				.perform(post(publicRequestPath + registerUserEndpoint).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerUserRequest)))
				.andExpect(status().is4xxClientError()).andReturn();
		response = new JSONObject(result.getResponse().getContentAsString());
		assert (response.getString(ERROR_CODE_KEY).equalsIgnoreCase(ApiError.INVALID_INPUT.getErrorCode()));
		assert (response.getString(MESSAGE_KEY).equalsIgnoreCase(ApiError.INVALID_INPUT.getErrorMessage()));

		// 3. Test Success with username and password
		registerUserRequest.setPassword(TEST_PASSWORD);
		registerUserRequest.setEmail(registerUserRequest.getUsername() + email_suffix);
		result = mockMvc
				.perform(post(publicRequestPath + registerUserEndpoint).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerUserRequest)))
				.andExpect(status().is2xxSuccessful()).andReturn();
		response = new JSONObject(result.getResponse().getContentAsString());
		assert (response.getString(USERNAME_KEY).equalsIgnoreCase(TEST_USERNAME));

		// 4. Test failure with existing user.
		registerUserRequest.setPassword(TEST_PASSWORD_2);
		result = mockMvc
				.perform(post(publicRequestPath + registerUserEndpoint).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerUserRequest)))
				.andExpect(status().is4xxClientError()).andReturn();
		response = new JSONObject(result.getResponse().getContentAsString());
		assert (response.getString(ERROR_CODE_KEY).equalsIgnoreCase(ApiError.USER_ALREADY_EXISTS.getErrorCode()));
		assert (response.getString(MESSAGE_KEY).equalsIgnoreCase(ApiError.USER_ALREADY_EXISTS.getErrorMessage()));

	}

	@Test
	void login() throws Exception {

		// 1. Register User
		RegisterUserRequest registerUserRequest = new RegisterUserRequest();
		registerUserRequest.setUsername(TEST_USERNAME_2);
		registerUserRequest.setPassword(TEST_PASSWORD);
		registerUserRequest.setExpectedCaloriesPerDay(11);
		registerUserRequest.setEmail(registerUserRequest.getUsername() + email_suffix);
		mockMvc.perform(post(publicRequestPath + registerUserEndpoint).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerUserRequest))).andExpect(status().is2xxSuccessful())
				.andReturn();

		// 2. Test Failure login with Wrong Password
		Map<String, String> params = new HashMap<>();
		params.put(USERNAME_KEY, registerUserRequest.getUsername());
		params.put(PASSWORD_KEY, WRONG_PASSWORD);
		mockMvc.perform(post(publicRequestPath + loginUserEndpoint).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(params))).andExpect(status().is4xxClientError()).andReturn();

		// 3. Test success login
		params.put(PASSWORD_KEY, registerUserRequest.getPassword());
		MvcResult result = mockMvc
				.perform(post(publicRequestPath + loginUserEndpoint).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(params)))
				.andExpect(status().is2xxSuccessful()).andReturn();

		assert (!result.getResponse().getHeader(HttpHeaders.AUTHORIZATION).isBlank());

	}

}
