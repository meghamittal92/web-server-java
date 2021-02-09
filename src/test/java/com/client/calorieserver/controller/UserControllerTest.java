package com.client.calorieserver.controller;

import com.client.calorieserver.configuration.security.JWTAuthorizationFilter;
import com.client.calorieserver.configuration.security.JWTUtil;
import com.client.calorieserver.configuration.security.SecurityConfigurer;
import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.mapper.UserMapperImpl;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.UserRepository;
import com.client.calorieserver.service.UserService;
import com.client.calorieserver.utils.UnsecuredWebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.servlet.ServletContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@UnsecuredWebMvcTest(value = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUserNoUserName() throws  Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setPassword("testPasssword");
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        JSONObject response_json = new JSONObject(result.getResponse().getContentAsString());
        assert (response_json.getString("errorCode").equalsIgnoreCase("E0003"));
        assert (response_json.getString("message").equalsIgnoreCase("Invalid input"));
    }

    @Test
    void createUserNoPasssword() throws  Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("");
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        JSONObject response_json = new JSONObject(result.getResponse().getContentAsString());
        assert (response_json.getString("errorCode").equalsIgnoreCase("E0003"));
        assert (response_json.getString("message").equalsIgnoreCase("Invalid input"));
    }

    @Test
    void createUserValidInput() throws  Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("password");
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(createUserRequest.getPassword());
        user.setId(12L);

        UserView userView = new UserView();
        userView.setUsername(user.getUsername());
        userView.setId(user.getId());
        userView.setExpectedCaloriesPerDay(user.getExpectedCaloriesPerDay());

        Mockito.when(userMapper.toUser(ArgumentMatchers.any(CreateUserRequest.class))).thenReturn(user);
        Mockito.when(userService.create(ArgumentMatchers.any(User.class))).thenReturn(user);
        Mockito.when(userMapper.toUserView(ArgumentMatchers.any(User.class))).thenReturn(userView);
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject response_json = new JSONObject(result.getResponse().getContentAsString());
        assert (response_json.getInt("id") == user.getId());
        assert (response_json.getString("username").equalsIgnoreCase(user.getUsername()));
    }

    @Test
    void getUsers() throws Exception{
        MvcResult result = mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        Mockito.verify(userService).findAll(Mockito.any(Pageable.class));
        Mockito.verify(userMapper).toUserView(ArgumentMatchers.anyList());
    }

    @Test
    void deleteUser() throws Exception{
        MvcResult result = mockMvc.perform(delete("/users/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(userService).deleteById(idCapture.capture());
        assert (idCapture.getValue() == 12L);
    }

    @Test
    void findUser() throws Exception{
        MvcResult result = mockMvc.perform(get("/users/test1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        ArgumentCaptor<String> idCapture = ArgumentCaptor.forClass(String.class);
        Mockito.verify(userService).findByUsername(idCapture.capture());
        assert (idCapture.getValue().equalsIgnoreCase("test1"));
    }

    @Test
    void replaceUserNoUserName() throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setPassword("testPasssword");
        MvcResult result = mockMvc.perform(put("/users/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createUserRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    void replaceUserNoPassword() throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        MvcResult result = mockMvc.perform(put("/users/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    void replaceUserValidParameters() throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(createUserRequest.getPassword());
        user.setId(12L);

        UserView userView = new UserView();
        userView.setUsername(user.getUsername());
        userView.setId(user.getId());
        userView.setExpectedCaloriesPerDay(user.getExpectedCaloriesPerDay());

        Mockito.when(userMapper.toUser(Mockito.any(CreateUserRequest.class))).thenReturn(user);
        Mockito.when(userService.replaceById(Mockito.anyLong(), Mockito.any(User.class))).thenReturn(user);
        Mockito.when(userMapper.toUserView(Mockito.any(User.class))).thenReturn(userView);

        MvcResult result = mockMvc.perform(put("/users/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject response_json = new JSONObject(result.getResponse().getContentAsString());
        assert (response_json.getInt("id") == user.getId());
        assert (response_json.getString("username").equalsIgnoreCase(user.getUsername()));
    }


}
