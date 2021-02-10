package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.LoginRequest;
import com.client.calorieserver.domain.dto.RegisterUserRequest;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerTest extends BaseIntegrationTest {

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

        registerUserRequest.setPassword("testPasssword");
        result = mockMvc.perform(post("/api/v1/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        response = new JSONObject(result.getResponse().getContentAsString());
        assert (response.getString("username").equalsIgnoreCase("user1"));


        registerUserRequest.setPassword("testPasssword2");
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
        registerUserRequest.setPassword("testPasssword");
        registerUserRequest.setExpectedCaloriesPerDay(11);
        mockMvc.perform(post("/api/v1/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        Map<String , String> params = new HashMap<>();
        params.put("username", registerUserRequest.getUsername());
        params.put("password", "wrong_password");
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
