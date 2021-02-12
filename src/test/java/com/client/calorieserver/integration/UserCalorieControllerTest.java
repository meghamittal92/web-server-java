package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.request.CreateUserCalorieRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserCalorieControllerTest extends BaseIntegrationTest {

	@Test
	void create() throws Exception {
		String token = registerUser("user1", "password1$", 120L);
		MvcResult result = mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(generateCalorieRequest(1, LocalDateTime.now(), "detail_1")))
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 1;
		assert jsonObject.getBoolean("withinLimit");

		result = mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(generateCalorieRequest(120, LocalDateTime.now(), "detail_2")))
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();

		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 120;
		assert !jsonObject.getBoolean("withinLimit");

		Map<String, String> createUserCalorieRequest = generateCalorieRequest(1, LocalDateTime.now().plusDays(2),
				"burger");
		createUserCalorieRequest.put("numCalories", null);
		result = mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createUserCalorieRequest))
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();

		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") > 0;
	}

	@Test
	void findById() throws Exception {
		String token = registerUser("user1", "password1$", 120L);
		Map<String, String> params = generateCalorieRequest(1, LocalDateTime.now(), "detail_1");
		MvcResult result = mockMvc
				.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 1;
		assert jsonObject.getBoolean("withinLimit");
		int id = jsonObject.getInt("id");

		result = mockMvc.perform(get(userCaloriesRequestPath + "/" + id).contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();

		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 1;
		assert jsonObject.getBoolean("withinLimit");
	}

	@Test
	void deleteById() throws Exception {
		String token = registerUser("user1", "password1$", 120L);
		Map<String, String> params = generateCalorieRequest(1, LocalDateTime.now(), "detail_1");
		MvcResult result = mockMvc
				.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 1;
		assert jsonObject.getBoolean("withinLimit");
		int id = jsonObject.getInt("id");

		String tokenNew = registerUser("user2", "password2$", 50L);
		params = generateCalorieRequest(100, LocalDateTime.now(), "detail_");
		result = mockMvc
				.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, tokenNew))
				.andExpect(status().is2xxSuccessful()).andReturn();

		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 100;
		assert !jsonObject.getBoolean("withinLimit");
		int otherId = jsonObject.getInt("id");

		result = mockMvc.perform(delete(userCaloriesRequestPath + "/" + otherId).contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is4xxClientError()).andReturn();

		result = mockMvc.perform(delete(userCaloriesRequestPath + "/" + id).contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();
	}

	@Test
	void UpdateCalorie() throws Exception {
		String token = registerUser("user1", "password1$", 10L);
		Map<String, String> params = generateCalorieRequest(5, LocalDateTime.now(), "detail_1");
		MvcResult result = mockMvc
				.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 5;
		assert jsonObject.getBoolean("withinLimit");
		int id = jsonObject.getInt("id");

		Map<String, String> newParams = generateCalorieRequest(9, LocalDateTime.now().plusDays(2), "detail_2");
		result = mockMvc
				.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newParams)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();

		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 9;
		assert jsonObject.getBoolean("withinLimit");
		int newId = jsonObject.getInt("id");

		params.put("dateTime", DateTimeFormatter.ISO_DATE_TIME
				.format(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS)));
		result = mockMvc
				.perform(patch(userCaloriesRequestPath + "/" + id).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 5;
		assert !jsonObject.getBoolean("withinLimit");

		result = mockMvc.perform(get(userCaloriesRequestPath + "/" + newId).contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 9L;
		assert !jsonObject.getBoolean("withinLimit");
	}

	@Test
	void ReplaceCalorie() throws Exception {
		String token = registerUser("user1", "password1$", 10L);
		Map<String, String> params = generateCalorieRequest(5, LocalDateTime.now(), "detail_1");
		MvcResult result = mockMvc
				.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 5;
		assert jsonObject.getBoolean("withinLimit");
		int id = jsonObject.getInt("id");

		Map<String, String> newParams = generateCalorieRequest(9, LocalDateTime.now(), "detail_2");
		result = mockMvc
				.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newParams)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();

		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 9L;
		assert !jsonObject.getBoolean("withinLimit");
		int newId = jsonObject.getInt("id");

		params.put("dateTime", DateTimeFormatter.ISO_DATE_TIME
				.format(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS)));
		result = mockMvc
				.perform(put(userCaloriesRequestPath + "/" + id).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 5;
		assert jsonObject.getBoolean("withinLimit");

		result = mockMvc.perform(get(userCaloriesRequestPath + "/" + newId).contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert jsonObject.getInt("numCalories") == 9L;
		assert jsonObject.getBoolean("withinLimit");
	}

	@Test
	void findAll() throws Exception {
		List<Map<String, String>> calories = new ArrayList<>();
		String token = registerUser("user1", "password1$", 10L);
		Map<String, String> params = generateCalorieRequest(1, LocalDateTime.now(), "detail_1");
		mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		calories.add(params);

		params = generateCalorieRequest(5, LocalDateTime.now().plusSeconds(10), "detail_2");
		mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		calories.add(params);

		params = generateCalorieRequest(10, LocalDateTime.now().plusDays(1), "detail_3");
		mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		calories.add(params);

		params = generateCalorieRequest(10, LocalDateTime.now().plusDays(1).plusSeconds(5), "detail_3");
		mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		calories.add(params);

		params = generateCalorieRequest(15, LocalDateTime.now().plusDays(1).plusSeconds(10), "detail_4");
		mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		calories.add(params);

		params = generateCalorieRequest(20, LocalDateTime.now().plusDays(3), "detail_5");
		mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		calories.add(params);

		params = generateCalorieRequest(25, LocalDateTime.now().plusDays(3).plusSeconds(2), "detail_6");
		mockMvc.perform(post(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(params)).header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		calories.add(params);

		MvcResult result = mockMvc.perform(get(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();
		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert (jsonObject.getJSONArray("content").length() == calories.size());

		result = mockMvc
				.perform(get(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.queryParam("search", "numCalories>=5").header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert (jsonObject.getJSONArray("content").length() == 6);

		result = mockMvc.perform(get(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
				.queryParam("search", "dateTime<=" + calories.get(4).get("dateTime") + " AND numCalories>=5")
				.header(HttpHeaders.AUTHORIZATION, token)).andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert (jsonObject.getJSONArray("content").length() == 4);

		result = mockMvc
				.perform(get(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.queryParam("search",
								"numCalories<10 AND dateTime<=" + calories.get(4).get("dateTime")
										+ " AND numCalories>=5")
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert (jsonObject.getJSONArray("content").length() == 1);

		result = mockMvc
				.perform(get(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.queryParam("search",
								"numCalories>=5 AND dateTime<=" + calories.get(5).get("dateTime")
										+ " OR numCalories>=10")
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert (jsonObject.getJSONArray("content").length() == 6);

		result = mockMvc
				.perform(get(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.queryParam("search",
								"numCalories<=10 AND dateTime<=" + calories.get(5).get("dateTime")
										+ " OR numCalories>=10")
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert (jsonObject.getJSONArray("content").length() == 4);

		result = mockMvc
				.perform(get(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.queryParam("search",
								"numCalories<=10 AND dateTime>=" + calories.get(5).get("dateTime")
										+ " OR numCalories>=10")
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert (jsonObject.getJSONArray("content").length() == 2);

		result = mockMvc
				.perform(get(userCaloriesRequestPath).contentType(MediaType.APPLICATION_JSON)
						.queryParam("search",
								"numCalories<=10 AND dateTime<=" + calories.get(5).get("dateTime")
										+ " OR withinLimit==true")
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful()).andReturn();
		jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assert (jsonObject.getJSONArray("content").length() == 4);
	}

	private Map<String, String> generateCalorieRequest(int calories, LocalDateTime time, String details) {
		Map<String, String> params = new HashMap<>();
		params.put("numCalories", String.valueOf(calories));
		params.put("dateTime", time.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
		params.put("mealDetails", details);
		return params;
	}

}
