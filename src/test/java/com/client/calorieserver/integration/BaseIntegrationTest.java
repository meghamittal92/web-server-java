package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/testdata/clean_all_tables.sql", "/testdata/create_roles.sql"})
public class BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    protected String createUser(String username, String password, Long expectedCaloriesPerDay) throws Exception{
        Map<String , String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("expectedCaloriesPerDay", expectedCaloriesPerDay.toString());
        mockMvc.perform(post("/api/v1/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Map<String , String> loginParams = new HashMap<>();
        loginParams.put("username", username);
        loginParams.put("password", password);
        MvcResult result = mockMvc.perform(post("/api/v1/public/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginParams)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return result.getResponse().getHeader("authorization");
    }

    protected String signIn(String username, String password) throws Exception{
        Map<String , String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        MvcResult result = mockMvc.perform(post("/api/v1/public/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return result.getResponse().getHeader("authorization");
    }
}
