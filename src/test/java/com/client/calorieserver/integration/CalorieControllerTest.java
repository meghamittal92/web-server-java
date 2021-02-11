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
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CalorieControllerTest extends BaseIntegrationTest{

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void create() throws Exception{
        String token = signIn("admin", "secretpassword");
        Long userId = createUser(token, "user1", 5);
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
        Long userId = createUser(token, "user1", 5);
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
        JSONObject jsonResult = new JSONObject(result.getResponse().getContentAsString());
        int calorieID = jsonResult.getInt("id");

        result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/calories/"+calorieID+1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/calories/"+calorieID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void update() throws Exception{
        String token = signIn("admin", "secretpassword");
        Long userId = createUser(token, "user1", 5);
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
        JSONObject jsonResult = new JSONObject(result.getResponse().getContentAsString());
        int calorieID = jsonResult.getInt("id");
        assert !jsonResult.getBoolean("withinLimit");

        params.put("numCalories", "5");
        params.remove("userId");
        result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/calories/"+calorieID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonResult = new JSONObject(result.getResponse().getContentAsString());
        assert jsonResult.getInt("id") == calorieID;
        assert jsonResult.getInt("numCalories") == 5;
        assert jsonResult.getBoolean("withinLimit");
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void find() throws Exception{
        String token = signIn("admin", "secretpassword");
        Long userId = createUser(token, "user1",5);
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
        JSONObject jsonResult = new JSONObject(result.getResponse().getContentAsString());
        int calorieID = jsonResult.getInt("id");
        assert !jsonResult.getBoolean("withinLimit");

        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/calories/"+calorieID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonResult = new JSONObject(result.getResponse().getContentAsString());
        assert jsonResult.getInt("id") == calorieID;
        assert jsonResult.getInt("numCalories") == 10;
        assert !jsonResult.getBoolean("withinLimit");
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void findAll() throws Exception{
        List<Map<String, String>> calories = new ArrayList<>();
        String token = signIn("admin", "secretpassword");
        Long userId = createUser(token, "user1", 10);
        Map<String, String> params = new HashMap<>();
        params.put("numCalories", "1");
        params.put("dateTime", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "detail_1");
        calories.add(params);

        mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        params = new HashMap<>();
        params.put("numCalories", "5");
        params.put("dateTime", LocalDateTime.now().plusSeconds(10).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "detail_2");
        calories.add(params);
        mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        userId = createUser(token, "user2", 10);
        params = new HashMap<>();
        params.put("numCalories", "10");
        params.put("dateTime", LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "detail_3");
        calories.add(params);
         mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        params = new HashMap<>();
        params.put("numCalories", "10");
        params.put("dateTime", LocalDateTime.now().plusDays(1).plusSeconds(5).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "detail_4");
        calories.add(params);
          mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        params = new HashMap<>();
        params.put("numCalories", "15");
        params.put("dateTime", LocalDateTime.now().plusDays(1).plusSeconds(10).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "detail_5");
        calories.add(params);
        mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        params = new HashMap<>();
        params.put("numCalories", "20");
        params.put("dateTime", LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "detail_6");
        calories.add(params);
        mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        params = new HashMap<>();
        params.put("numCalories", "25");
        params.put("dateTime", LocalDateTime.now().plusDays(3).plusSeconds(2).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        params.put("userId", userId.toString());
        params.put("mealDetails", "detail_7");
        calories.add(params);
        mockMvc.perform(post("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 7);

        result = mockMvc.perform(get("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("search", "numCalories>=5")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 6);

        result = mockMvc.perform(get("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("search", "dateTime<="+calories.get(4).get("dateTime")+" AND numCalories>=5")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 4);

        result = mockMvc.perform(get("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("search", "numCalories<10 AND dateTime<="+calories.get(4).get("dateTime")+" AND numCalories>=5")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 1);

        result = mockMvc.perform(get("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("search", "numCalories>=5 AND dateTime<="+calories.get(5).get("dateTime")+" OR numCalories>=10")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 6);

        result = mockMvc.perform(get("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("search", "numCalories<=10 AND dateTime<="+calories.get(5).get("dateTime")+" OR numCalories>=10")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 4);

        result = mockMvc.perform(get("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("search", "numCalories<=10 AND dateTime>="+calories.get(5).get("dateTime")+" OR numCalories>=10")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 2);

        result = mockMvc.perform(get("/api/v1/calories")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("search", "numCalories<=10 AND dateTime<="+calories.get(5).get("dateTime")+" OR withinLimit==true")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 4);
    }


    private Long createUser(String token, String username, Integer numCalories) throws Exception{
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername(username);
        userRequest.setPassword("password1");
        userRequest.setExpectedCaloriesPerDay(numCalories);
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
        assert jsonObject.getString("username").equalsIgnoreCase(username);
        return jsonObject.getLong("id");
    }

}
