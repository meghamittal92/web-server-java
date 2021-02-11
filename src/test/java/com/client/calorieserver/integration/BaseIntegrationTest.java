package com.client.calorieserver.integration;

import com.client.calorieserver.configuration.db.JpaAuditingConfiguration;
import com.client.calorieserver.domain.dto.request.CreateUserRequest;
import com.client.calorieserver.repository.CalorieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/testdata/clean_all_tables.sql", "/testdata/create_roles.sql"})
public class BaseIntegrationTest {

    public static final String email_suffix = "@gmail.com";
    public static final String default_username = "test_user";
    public static final Long default_expected_calories = 10L;
    public static final String default_password = "test_password$1";
    public static final String[] default_roles = {"USER"};
    protected static final String ADMIN_USERNAME = "admin";
    protected static final String ADMIN_PASSWORD = "secretpassword";


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${server.request.path.users}")
    protected String usersRequestPath;
    @Value("${server.request.path.calories}")
    protected String caloriesRequestPath;
    @Value("${server.request.path.userProfile.calories}")
    protected String userCaloriesRequestPath;
    @Value("${server.request.path.userProfile}")
    protected String userProfileRequestPath;
    @Value("${server.request.path.public}")
    protected String publicRequestPath;
    @Value("${server.request.endpoint.registerUser}")
    protected String registerUserEndpoint;
    @Value("${server.request.endpoint.loginUser}")
    protected String loginUserEndpoint;

    protected String registerUser(String username, String password, Long expectedCaloriesPerDay) throws Exception{
        Map<String , String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("expectedCaloriesPerDay", expectedCaloriesPerDay.toString());
        params.put("email", username+email_suffix);

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


    protected String createUser(String token, String username, String password,
                              int expectedCaloriesPerDay, String... roles) throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setEmail(username+email_suffix);
        Set<String> rolesSet = new HashSet<>(Arrays.asList(roles));
        createUserRequest.setRoles(rolesSet);
        createUserRequest.setExpectedCaloriesPerDay(expectedCaloriesPerDay);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return result.getResponse().getContentAsString();
    }
}
