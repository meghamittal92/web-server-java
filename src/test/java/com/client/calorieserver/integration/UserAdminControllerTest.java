package com.client.calorieserver.integration;

import com.client.calorieserver.domain.dto.request.CreateUserRequest;
import com.client.calorieserver.domain.dto.request.UpdateUserRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserAdminControllerTest extends BaseIntegrationTest{


    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void getAllUser() throws Exception{
        registerUser("user1", "password1$", 100L);
        registerUser("user2", "password2$", 200L);
        registerUser("user3", "password3$", 300L);
        String token = signIn(ADMIN_USERNAME, ADMIN_PASSWORD);

        MvcResult result = mockMvc.perform(get(usersRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("sort", "username")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        JSONObject resultJson = new JSONObject(result.getResponse().getContentAsString());
        JSONArray users = resultJson.getJSONArray("content");
        assert (users.length() == 4);
        assert (users.getJSONObject(0).getString("username").equalsIgnoreCase(ADMIN_USERNAME));
        assert (users.getJSONObject(1).getString("username").equalsIgnoreCase("user1"));
        assert (users.getJSONObject(2).getString("username").equalsIgnoreCase("user2"));
        assert (users.getJSONObject(3).getString("username").equalsIgnoreCase("user3"));

        result = mockMvc.perform(get(usersRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page","0")
                .queryParam("size", "2")
                .queryParam("sort", "username")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        resultJson = new JSONObject(result.getResponse().getContentAsString());
        users = resultJson.getJSONArray("content");
        assert (users.length() == 2);
        assert (users.getJSONObject(0).getString("username").equalsIgnoreCase(ADMIN_USERNAME));
        assert (users.getJSONObject(1).getString("username").equalsIgnoreCase("user1"));

        result = mockMvc.perform(get(usersRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("sort", "username")
                .queryParam("page","1")
                .queryParam("size", "2")
                .header(HttpHeaders.AUTHORIZATION, token))
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
        String token = signIn(ADMIN_USERNAME, ADMIN_PASSWORD);
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername(default_username);
        userRequest.setPassword("invalidpassword");
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        userRequest.setRoles(roles);
        userRequest.setExpectedCaloriesPerDay(120);

        mockMvc.perform(post(usersRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        userRequest.setEmail(userRequest.getUsername()+email_suffix);
        mockMvc.perform(post(usersRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        userRequest.setPassword(default_password);
        MvcResult result = mockMvc.perform(post(usersRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getString("username").equalsIgnoreCase(default_username);
        assert jsonObject.getJSONArray("roles").get(0).toString().equalsIgnoreCase("USER");
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void findById() throws Exception{
        String token = signIn(ADMIN_USERNAME, ADMIN_PASSWORD);
        String response = createUser(token, default_username, default_password, 120, default_roles);
        JSONObject jsonObject = new JSONObject(response);
        int id = jsonObject.getInt("id");


        MvcResult result = mockMvc.perform(get(usersRequestPath + "/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject resultJson = new JSONObject(result.getResponse().getContentAsString());
        assert resultJson.getString("username").equalsIgnoreCase(default_username);
        assert resultJson.getInt("id") == id;
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void findAll() throws Exception{
        String token = signIn(ADMIN_USERNAME, ADMIN_PASSWORD);
        String response = createUser(token, default_username, default_password, 120, default_roles);


        MvcResult result = mockMvc.perform(get(usersRequestPath )
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("search", "username==" + default_username)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert (jsonObject.getJSONArray("content").length() == 1);
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void deleteById() throws Exception{
        String token = signIn(ADMIN_USERNAME, ADMIN_PASSWORD);
        String response = createUser(token, default_username, default_password, 120, default_roles);
        JSONObject jsonObject = new JSONObject(response);
        int id = jsonObject.getInt("id");


        mockMvc.perform(delete(usersRequestPath + "/"+123)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        mockMvc.perform(delete(usersRequestPath + "/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void replaceByid() throws Exception{
        String token = signIn(ADMIN_USERNAME, ADMIN_PASSWORD);
        String response = createUser(token, default_username, default_password, 10, default_roles);
        JSONObject jsonObject = new JSONObject(response);
        int id = jsonObject.getInt("id");

        createUser(token, "user2", "password2$", 10, default_roles);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setPassword(default_password);
        userRequest.setUsername("user2");
        userRequest.setEmail("newemail@gmail.com");

        mockMvc.perform(put(usersRequestPath + "/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is4xxClientError())
                .andReturn();

        userRequest.setUsername(default_username);
        userRequest.setExpectedCaloriesPerDay(125);
        MvcResult result = mockMvc.perform(put(usersRequestPath + "/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("expectedCaloriesPerDay") == 125;
    }


    @Test
    @Sql({"/testdata/create_admin.sql"})
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void UpdateUser() throws Exception{
        String token = signIn(ADMIN_USERNAME, ADMIN_PASSWORD);
        String response = createUser(token, default_username, default_password, 10, default_roles);
        JSONObject jsonObject = new JSONObject(response);
        int id = jsonObject.getInt("id");

        createUser(token, "user2", "password2$", 10, default_roles);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setPassword(default_password);
        userRequest.setEmail("newemail@gmail.com");
        userRequest.setUsername("user2");

        mockMvc.perform(patch(usersRequestPath + "/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is4xxClientError())
                .andReturn();


        userRequest.setUsername(default_username);
        userRequest.setExpectedCaloriesPerDay(125);
        MvcResult result = mockMvc.perform(patch(usersRequestPath + "/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jsonObject = new JSONObject(result.getResponse().getContentAsString());
        assert jsonObject.getInt("expectedCaloriesPerDay") == 125;
        assert jsonObject.getString("email").equalsIgnoreCase("newemail@gmail.com");
    }


}
