package com.client.calorieserver.controller;

import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.mapper.UserMapperImpl;
import com.client.calorieserver.repository.UserRepository;
import com.client.calorieserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

public class UserControllerTest {

    private UserService userService;
    private UserMapper userMapper;
    private UserRepository userRepository;
    @BeforeEach
    void initController(){
        userMapper = new UserMapperImpl();
        userService = new UserService(userRepository, userMapper);
    }
    
}
