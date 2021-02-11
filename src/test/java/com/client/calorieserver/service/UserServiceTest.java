package com.client.calorieserver.service;


import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.exception.EntityAlreadyExistsException;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;


public class UserServiceTest {

    private static final String username = "test_user";
    private static final String password = "test_password";
    private static final String email = "test_email@gmail.com";

    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    public void createUserNameExists(){
        User user = new User();
        user.setUsername("test1");
        user.setPassword("password");

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
        Assertions.assertThrows(EntityAlreadyExistsException.class, () -> {
            userService.create(user);
        });
    }

    @Test
    public void createUser(){
        User user = generateUser();

        UserDTO userDTO = generateUserDTO();

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
        Mockito.when(userMapper.toUserDTO(Mockito.any(User.class))).thenReturn(userDTO);
        Mockito.when(userRepository.save(Mockito.any(UserDTO.class))).thenReturn(userDTO);
        Mockito.when(userMapper.toUser(Mockito.any(UserDTO.class))).thenReturn(user);

        Assertions.assertThrows(EntityAlreadyExistsException.class, () -> {
            User savedUser = userService.create(user);

        });
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
        Assertions.assertThrows(EntityAlreadyExistsException.class, () -> {
            User savedUser = userService.create(user);
        });

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        User savedUser = userService.create(user);
        Assertions.assertEquals(savedUser.getUsername(), user.getUsername());
        Assertions.assertEquals(savedUser.getPassword(), user.getPassword());
    }

    @Test
    public void replaceByIdNoEntity(){
        User user = new User();
        user.setUsername("test1");
        user.setPassword("password");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.replaceById(12L, user);
        });
    }

    @Test
    public void replaceByIdDuplicateEntity(){
        User user = new User();
        user.setUsername("test1");
        user.setPassword("password");

        UserDTO original = new UserDTO();
        original.setUsername("test2");
        original.setPassword("password");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(original));
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
        Assertions.assertThrows(EntityAlreadyExistsException.class, () -> {
            userService.replaceById(12L, user);
        });
    }

    @Test
    public void replaceById(){
        User user = generateUser();
        user.setUsername("test_user_1");
        user.setEmail("newemail@gmail.com");

        UserDTO original = generateUserDTO();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->{
            userService.replaceById(12L ,user);
        });

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(original));
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
        Assertions.assertThrows(EntityAlreadyExistsException.class, () ->{
            userService.replaceById(12L ,user);
        });

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(EntityAlreadyExistsException.class, () ->{
            userService.replaceById(12L ,user);
        });
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(userMapper.toUserDTO(Mockito.any(User.class))).thenReturn(original);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(original);
        Mockito.when(userMapper.toUser(Mockito.any(UserDTO.class))).thenReturn(user);
        original.setUsername(user.getUsername());
        original.setEmail(user.getEmail());
        userService.replaceById(12L ,user);
        Mockito.verify(userMapper).toUser(Mockito.any(UserDTO.class));
    }

    @Test
    public void findByUserNameNoEntity(){
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("test");
        });
    }

    @Test
    public void findByUserName(){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("password");
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(userDTO));
        Mockito.when(userMapper.toUser(Mockito.any(UserDTO.class))).thenReturn(new User());
        userService.loadUserByUsername("test");
        Mockito.verify(userMapper).toUser(Mockito.any(UserDTO.class));
    }

    @Test
    public void deletByUserIdNoEntity(){
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteById(12L);
        });
    }

    @Test
    public void deletByUserId(){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("password");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userDTO));
        userService.deleteById(12L);
        Mockito.verify(userRepository).deleteById(Mockito.anyLong());

    }

    @Test
    public void loadUserNoUserName(){
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("test");
        });
    }

    @Test
    public void loadUserByUserName(){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("password");

        User user = new User();
        user.setUsername("test");
        user.setPassword("password");

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(userDTO));
        Mockito.when(userMapper.toUser(Mockito.any(UserDTO.class))).thenReturn(user);
        userService.loadUserByUsername("test");
        Mockito.verify(userMapper).toUser(Mockito.any(UserDTO.class));
    }

    @Test
    public void findAllUsers(){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("password");

        User user = new User();
        user.setUsername("test");
        user.setPassword("password");

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(userDTO));
        Mockito.when(userMapper.toUser(Mockito.any(UserDTO.class))).thenReturn(user);
        userService.loadUserByUsername("test");
        Mockito.verify(userMapper).toUser(Mockito.any(UserDTO.class));
    }

    @Test
    public void updateById(){
        User user = generateUser();
        user.setUsername("test_user_1");
        user.setEmail("newemail@gmail.com");

        UserDTO original = generateUserDTO();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->{
            userService.updateById(12L ,user);
        });

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(original));
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
        Assertions.assertThrows(EntityAlreadyExistsException.class, () ->{
            userService.updateById(12L ,user);
        });

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(EntityAlreadyExistsException.class, () ->{
            userService.updateById(12L ,user);
        });
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(userMapper.toUserDTO(Mockito.any(User.class))).thenReturn(original);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(original);
        Mockito.when(userMapper.toUser(Mockito.any(UserDTO.class))).thenReturn(user);
        original.setUsername(user.getUsername());
        original.setEmail(user.getEmail());
        userService.updateById(12L ,user);
        Mockito.verify(userMapper).toUser(Mockito.any(UserDTO.class));
    }

    @Test
    public void findById(){
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.findById(12L);
        });
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(generateUserDTO()));
        Mockito.when(userMapper.toUser(Mockito.any(UserDTO.class))).thenReturn(generateUser());
        userService.findById(12L);
    }

    private User generateUser(){
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    private UserDTO generateUserDTO(){
        UserDTO user = new UserDTO();
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }


}
