package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.request.RegisterUserRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerTest extends BaseIntegrationTest {

    private static final String email_suffix = "@gmail.com";
    @Test
    void createUser() throws Exception{
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        MvcResult result = mockMvc.perform(post("/api/v1/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        registerUserRequest.setUsername("user1");
        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assert (response.getString("errorCode").equalsIgnoreCase("E0003"));
        assert (response.getString("message").equalsIgnoreCase("invalid input"));

        result = mockMvc.perform(post("/api/v1/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        response = new JSONObject(result.getResponse().getContentAsString());
        assert (response.getString("errorCode").equalsIgnoreCase("E0003"));
        assert (response.getString("message").equalsIgnoreCase("invalid input"));

        registerUserRequest.setPassword("testPasssword1$");
        registerUserRequest.setEmail(registerUserRequest.getUsername()+email_suffix);
        result = mockMvc.perform(post("/api/v1/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        response = new JSONObject(result.getResponse().getContentAsString());
        assert (response.getString("username").equalsIgnoreCase("user1"));


        registerUserRequest.setPassword("testPasssword2$");
        result = mockMvc.perform(post("/api/v1/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        response = new JSONObject(result.getResponse().getContentAsString());
        assert (response.getString("message").equalsIgnoreCase("User already exists"));
    }

    @Test
    void login() throws Exception{

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("login_user");
        registerUserRequest.setPassword("testPasssword2$");
        registerUserRequest.setExpectedCaloriesPerDay(11);
        registerUserRequest.setEmail(registerUserRequest.getUsername()+email_suffix);
        mockMvc.perform(post("/api/v1/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        Map<String , String> params = new HashMap<>();
        params.put("username", registerUserRequest.getUsername());
        params.put("password", "wrong_password1$");
        mockMvc.perform(post("/api/v1/public/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        params.put("password", registerUserRequest.getPassword());
        MvcResult result = mockMvc.perform(post("/api/v1/public/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assert (!result.getResponse().getHeader("authorization").isBlank());

    }

}
