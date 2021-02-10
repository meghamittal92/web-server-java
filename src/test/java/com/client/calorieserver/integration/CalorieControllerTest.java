package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.CreateCalorieRequest;
import com.client.calorieserver.domain.dto.CreateUserRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CalorieControllerTest extends BaseIntegrationTest{

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void create() throws Exception{
        String token = signIn("admin", "secretpassword");
        Long userId = createUser(token);
        Map<String, String> params = new HashMap<>();
        params.put("numCalories", "10");
        params.put("dateTime", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "first_meal");

        MvcResult result = mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonResponse = new JSONObject(result.getResponse().getContentAsString());
        assert jsonResponse.getString("mealDetails").equalsIgnoreCase(params.get("mealDetails"));
        assert !jsonResponse.getBoolean("withinLimit");
        assert jsonResponse.getLong("userId") == userId;
        assert jsonResponse.getInt("numCalories") == Integer.parseInt(params.get("numCalories"));
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void delete() throws Exception{
        String token = signIn("admin", "secretpassword");
        Long userId = createUser(token);
        Map<String, String> params = new HashMap<>();
        params.put("numCalories", "10");
        params.put("dateTime", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "first_meal");

        MvcResult result = mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    private Long createUser(String token) throws Exception{
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("user1");
        userRequest.setPassword("password1");
        userRequest.setExpectedCaloriesPerDay(5);
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        userRequest.setRoles(roles);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getString("username").equalsIgnoreCase("user1");
        return jsonObject.getLong("id");
    }
}
