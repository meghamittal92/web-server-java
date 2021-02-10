package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.UpdateUserRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserAdminControllerTest extends BaseIntegrationTest{


    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void getAllUser() throws Exception{
        createUser("user1", "password1", 100L);
        createUser("user2", "password2", 200L);
        createUser("user3", "password3", 300L);
        String token = signIn("admin", "secretpassword");

        MvcResult result = mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("sort", "username")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject resultJson = new JSONObject(result.getResponse().getContentAsString());
        JSONArray users = resultJson.getJSONArray("content");
        assert (users.length() == 4);
        assert (users.getJSONObject(0).getString("username").equalsIgnoreCase("admin"));
        assert (users.getJSONObject(1).getString("username").equalsIgnoreCase("user1"));
        assert (users.getJSONObject(2).getString("username").equalsIgnoreCase("user2"));
        assert (users.getJSONObject(3).getString("username").equalsIgnoreCase("user3"));

        result = mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page","0")
                .queryParam("size", "2")
                .queryParam("sort", "username")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        resultJson = new JSONObject(result.getResponse().getContentAsString());
        users = resultJson.getJSONArray("content");
        assert (users.length() == 2);
        assert (users.getJSONObject(0).getString("username").equalsIgnoreCase("admin"));
        assert (users.getJSONObject(1).getString("username").equalsIgnoreCase("user1"));

        result = mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("sort", "username")
                .queryParam("page","1")
                .queryParam("size", "2")
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        resultJson = new JSONObject(result.getResponse().getContentAsString());
        users = resultJson.getJSONArray("content");
        assert (users.length() == 2);
        assert (users.getJSONObject(0).getString("username").equalsIgnoreCase("user2"));
        assert (users.getJSONObject(1).getString("username").equalsIgnoreCase("user3"));
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void createUser() throws Exception{
        String token = signIn("admin", "secretpassword");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("user1");
        userRequest.setPassword("password1");
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        userRequest.setRoles(roles);
        userRequest.setExpectedCaloriesPerDay(120);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getString("username").equalsIgnoreCase("user1");
        assert jsonObject.getJSONArray("roles").get(0).toString().equalsIgnoreCase("ADMIN");
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void findById() throws Exception{
        String token = signIn("admin", "secretpassword");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("user1");
        userRequest.setPassword("password1");
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        userRequest.setRoles(roles);
        userRequest.setExpectedCaloriesPerDay(120);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        int id = jsonObject.getInt("id");


        result = mockMvc.perform(get("/api/v1/users/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject resultJson = new JSONObject(result.getResponse().getContentAsString());
        assert resultJson.getString("username").equalsIgnoreCase("user1");
        assert resultJson.getInt("id") == id;
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void deleteById() throws Exception{
        String token = signIn("admin", "secretpassword");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("user1");
        userRequest.setPassword("password1");
        userRequest.setExpectedCaloriesPerDay(120);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        int id = jsonObject.getInt("id");


        mockMvc.perform(delete("/api/v1/users/"+123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        mockMvc.perform(delete("/api/v1/users/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void replaceByid() throws Exception{
        String token = signIn("admin", "secretpassword");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("user1");
        userRequest.setPassword("password1");
        userRequest.setExpectedCaloriesPerDay(120);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        int id = jsonObject.getInt("id");

        userRequest = new CreateUserRequest();
        userRequest.setUsername("user2");
        userRequest.setPassword("password2");
        userRequest.setExpectedCaloriesPerDay(120);

         mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        mockMvc.perform(put("/api/v1/users/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        userRequest.setUsername("user1");
        userRequest.setExpectedCaloriesPerDay(125);
        result = mockMvc.perform(put("/api/v1/users/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("expectedCaloriesPerDay") == 125;
    }


    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void UpdateUser() throws Exception{
        String token = signIn("admin", "secretpassword");
        UpdateUserRequest userRequest = new UpdateUserRequest();
        userRequest.setUsername("user1");
        userRequest.setPassword("password1");
        userRequest.setExpectedCaloriesPerDay(120);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        int id = jsonObject.getInt("id");

        userRequest = new UpdateUserRequest();
        userRequest.setUsername("user2");
        userRequest.setPassword("password2");
        userRequest.setExpectedCaloriesPerDay(120);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        mockMvc.perform(patch("/api/v1/users/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        userRequest.setUsername("user1");
        userRequest.setExpectedCaloriesPerDay(125);
        result = mockMvc.perform(patch("/api/v1/users/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("authorization", token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("expectedCaloriesPerDay") == 125;
    }


}
