package com.client.calorieserver.controller;

import com.client.calorieserver.configuration.TestConfiguration;
import com.client.calorieserver.configuration.security.JWTAuthorizationFilter;
import com.client.calorieserver.configuration.security.JWTUtil;
import com.client.calorieserver.configuration.security.SecurityConfigurer;
import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.mapper.UserMapperImpl;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.*;
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
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.LinkedMultiValueMap;

import javax.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@UnsecuredWebMvcTest(value = UserAdminController.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
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
        MvcResult result = mockMvc.perform(post("/api/v1/users")
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
        MvcResult result = mockMvc.perform(post("/api/v1/users")
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
        MvcResult result = mockMvc.perform(post("/api/v1/users")
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
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page","0");
        requestParams.add("size", "2");
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        user.setId(12L);
        users.add(user);

        user = new User();
        user.setUsername("test1");
        user.setPassword("password1");
        user.setId(13L);
        users.add(user);


        List<UserView> userViews = new ArrayList<>();
        UserView userView = new UserView();
        userView.setUsername("test");
        userView.setId(12L);
        userViews.add(userView);

        userView = new UserView();
        userView.setUsername("test1");
        userView.setId(13L);
        userViews.add(userView);


        Page<User> pagedUsers = new PageImpl<User>(users);

        Mockito.when(userService.findAll(Mockito.any(Pageable.class))).thenReturn(pagedUsers);
        Mockito.when(userMapper.toUserView(ArgumentMatchers.eq(users.get(0)))).thenReturn(userViews.get(0));
        Mockito.when(userMapper.toUserView(ArgumentMatchers.eq(users.get(1)))).thenReturn(userViews.get(1));
        MvcResult result = mockMvc.perform(get("/api/v1/users")
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Mockito.verify(userService).findAll(Mockito.any(Pageable.class));
        Mockito.verify(userMapper, Mockito.atLeast(2)).toUserView(Mockito.any(User.class));
    }

    @Test
    void deleteUser() throws Exception{
        MvcResult result = mockMvc.perform(delete("/api/v1/users/12")
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
        MvcResult result = mockMvc.perform(get("/api/v1/users/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(userService).findById(idCapture.capture());
        assert (idCapture.getValue() == 12);
    }

    @Test
    void replaceUserNoUserName() throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setPassword("testPasssword");
        MvcResult result = mockMvc.perform(put("/api/v1/users/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createUserRequest)))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    void replaceUserNoPassword() throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        MvcResult result = mockMvc.perform(put("/api/v1/users/12")
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

        MvcResult result = mockMvc.perform(put("/api/v1/users/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        JSONObject response_json = new JSONObject(result.getResponse().getContentAsString());
        assert (response_json.getInt("id") == user.getId());
        assert (response_json.getString("username").equalsIgnoreCase(user.getUsername()));
    }


}
