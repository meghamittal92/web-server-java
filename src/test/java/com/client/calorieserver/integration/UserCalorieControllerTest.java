package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.CreateCalorieRequest;
import com.client.calorieserver.domain.dto.CreateUserCalorieRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserCalorieControllerTest extends BaseIntegrationTest{

    @Test
    void create() throws Exception{
        String token = createUser("user1", "password1", 120L);
        MvcResult result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generateCalorieRequest(
                        1, LocalDateTime.now(), "detail_1")))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 1;
        assert jsonObject.getBoolean("withinLimit");

        result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generateCalorieRequest(
                        120, LocalDateTime.now(), "detail_1")))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 120;
        assert !jsonObject.getBoolean("withinLimit");
    }

    @Test
    void findById() throws Exception{
        String token = createUser("user1", "password1", 120L);
        Map<String, String> params = generateCalorieRequest(1, LocalDateTime.now(), "detail_1");
        MvcResult result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 1;
        assert jsonObject.getBoolean("withinLimit");
        int id = jsonObject.getInt("id");

        result = mockMvc.perform(get("/api/v1/profile/calories/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 1;
        assert jsonObject.getBoolean("withinLimit");
    }

    @Test
    void deleteById() throws Exception{
        String token = createUser("user1", "password1", 120L);
        Map<String, String> params = generateCalorieRequest(1, LocalDateTime.now(), "detail_1");
        MvcResult result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 1;
        assert jsonObject.getBoolean("withinLimit");
        int id = jsonObject.getInt("id");

        String tokenNew = createUser("user2", "password2", 50L);
        params = generateCalorieRequest(100, LocalDateTime.now(), "detail_");
        result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", tokenNew))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 100;
        assert !jsonObject.getBoolean("withinLimit");
        int otherId = jsonObject.getInt("id");

        result = mockMvc.perform(delete("/api/v1/profile/calories/"+otherId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        result = mockMvc.perform(delete("/api/v1/profile/calories/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void UpdateCalorie() throws Exception{
        String token = createUser("user1", "password1", 10L);
        Map<String, String> params = generateCalorieRequest(1, LocalDateTime.now(), "detail_1");
        MvcResult result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 1;
        assert jsonObject.getBoolean("withinLimit");
        int id = jsonObject.getInt("id");

        Map<String, String> newParams = generateCalorieRequest(11, LocalDateTime.now(), "detail_2");
        result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newParams))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 11L;
        assert !jsonObject.getBoolean("withinLimit");
        int newId = jsonObject.getInt("id");

        params.put("dateTime", DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now().plusDays(2)));
        result = mockMvc.perform(patch("/api/v1/profile/calories/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 1;
        assert jsonObject.getBoolean("withinLimit");

        result = mockMvc.perform(get("/api/v1/profile/calories/"+newId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 11L;
        assert jsonObject.getBoolean("withinLimit");
    }


    @Test
    void ReplaceCalorie() throws Exception{
        String token = createUser("user1", "password1", 10L);
        Map<String, String> params = generateCalorieRequest(1, LocalDateTime.now(), "detail_1");
        MvcResult result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 1;
        assert jsonObject.getBoolean("withinLimit");
        int id = jsonObject.getInt("id");

        Map<String, String> newParams = generateCalorieRequest(11, LocalDateTime.now(), "detail_2");
        result = mockMvc.perform(post("/api/v1/profile/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newParams))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 11L;
        assert !jsonObject.getBoolean("withinLimit");
        int newId = jsonObject.getInt("id");

        params.put("dateTime", DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now().plusDays(2)));
        result = mockMvc.perform(put("/api/v1/profile/calories/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 1;
        assert jsonObject.getBoolean("withinLimit");

        result = mockMvc.perform(get("/api/v1/profile/calories/"+newId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("numCalories") == 11L;
        assert jsonObject.getBoolean("withinLimit");
    }


    private Map<String, String> generateCalorieRequest(int calories, LocalDateTime time, String details){
        Map<String, String> params = new HashMap<>();
        params.put("numCalories",String.valueOf(calories));
        params.put("dateTime", DateTimeFormatter.ISO_DATE_TIME.format(time));
        params.put("mealDetails", details);
        return params;
    }

}
