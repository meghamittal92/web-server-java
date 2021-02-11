package com.client.calorieserver.integration;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserProfileControllerTest extends BaseIntegrationTest{

    @Test
    void readProfile() throws Exception{
        String token = registerUser("user1", "password1$", 2000L);
        mockMvc.perform(get(userProfileRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "wrong_token"))
                .andExpect(status().is4xxClientError())
                .andReturn();

        MvcResult result = mockMvc.perform(get(userProfileRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assert (response.getString("username").equalsIgnoreCase("user1"));
    }

    @Test
    void updateProfile() throws Exception{
        String token = registerUser("user1", "password1$", 1000L);
        Map<String, String> params = new HashMap<>();
        mockMvc.perform(patch(userProfileRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        params.put("expectedCaloriesPerDay", "1001");
        MvcResult result = mockMvc.perform(patch(userProfileRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assert (response.getInt("expectedCaloriesPerDay") == 1001);
    }
}