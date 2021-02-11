package com.client.calorieserver.mapper;


import com.client.calorieserver.domain.dto.db.RoleDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.request.CreateUserRequest;
import com.client.calorieserver.domain.dto.response.UserView;
import com.client.calorieserver.domain.mapper.UserMapperImpl;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

public class UserMapperImplTest {

    private UserMapperImpl userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        userMapper = new UserMapperImpl();
        userMapper.setPasswordEncoder(passwordEncoder);
        userMapper.setRoleRepository(roleRepository);
    }

    @Test
    void toUserView(){
        User user = generateUser("test_user", "test_pass", 12L);
        UserView userView = userMapper.toUserView(user);
        assert (userView.getUsername().equals(user.getUsername()));
        assert (userView.getId().equals(user.getId()));
    }

    @Test
    void toUserListView(){
        User user1 = generateUser("test_user1", "test_pass1", 12L);
        User user2 = generateUser("test_user2", "test_pass2", 13L);
        List<User> users = List.of(user1, user2);
        List<UserView> userViews = userMapper.toUserView(users);
        assert (userViews.get(0).getUsername().equals(user1.getUsername()));
        assert (userViews.get(0).getId().equals(user1.getId()));
        assert (userViews.get(1).getUsername().equals(user2.getUsername()));
        assert (userViews.get(1).getId().equals(user2.getId()));
    }

    @Test
    void toUser(){
        Set<String> roles = new HashSet<>();
        roles.add("xyz");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("password");
        userRequest.setRoles(roles);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
        User user = userMapper.toUser(userRequest);
        assert (user.getUsername().equals(userRequest.getUsername()));
        assert (user.getPassword().equalsIgnoreCase("encodedPassword"));
        assert (user.getRoles().contains(Role.USER));
    }

    @Test
    void toUserDTO(){
        User user = generateUser("test", "password", 12L);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("user");
        Mockito.when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(roleDTO));
        UserDTO userDTO = userMapper.toUserDTO(user);
        assert (userDTO.getUsername().equalsIgnoreCase(user.getUsername()));
        assert (userDTO.getPassword().equalsIgnoreCase(user.getPassword()));
        assert (userDTO.getRoleDTOs().contains(roleDTO));
    }

    @Test
    void toUserFromUserDTO(){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("password");
        userDTO.setId(12L);
        RoleDTO userRoleDTO = new RoleDTO();
        userRoleDTO.setName("USER");
        RoleDTO managerRoleDTO = new RoleDTO();
        managerRoleDTO.setName("USER_MANAGER");

        Set<RoleDTO> roleDTOS = new HashSet<>();
        roleDTOS.add(userRoleDTO);
        roleDTOS.add(managerRoleDTO);
        userDTO.setRoleDTOs(roleDTOS);

        User user = userMapper.toUser(userDTO);
        assert (user.getUsername().equalsIgnoreCase(userDTO.getUsername()));
        assert (user.getPassword().equalsIgnoreCase(userDTO.getPassword()));
        assert (user.getRoles().contains(Role.USER));
        assert (user.getRoles().contains(Role.USER_MANAGER));
    }

    @Test
    void toUsersFromUserDTOs(){
        UserDTO user1DTO = new UserDTO();
        user1DTO.setUsername("test1");
        user1DTO.setPassword("password1");
        user1DTO.setId(12L);
        RoleDTO user1RoleDTO = new RoleDTO();
        user1RoleDTO.setName("USER");
        RoleDTO user1managerRoleDTO = new RoleDTO();
        user1managerRoleDTO.setName("USER_MANAGER");
        Set<RoleDTO> user1roleDTOS = new HashSet<>();
        user1roleDTOS.add(user1RoleDTO);
        user1roleDTOS.add(user1managerRoleDTO);
        user1DTO.setRoleDTOs(user1roleDTOS);

        UserDTO user2DTO = new UserDTO();
        user2DTO.setUsername("test2");
        user2DTO.setPassword("password2");
        user2DTO.setId(12L);
        RoleDTO user2RoleDTO = new RoleDTO();
        user2RoleDTO.setName("ADMIN");
        Set<RoleDTO> user2roleDTOS = new HashSet<>();
        user2roleDTOS.add(user2RoleDTO);
        user2DTO.setRoleDTOs(user2roleDTOS);
        List<UserDTO> userDTOS = new ArrayList<>();
        userDTOS.add(user1DTO);
        userDTOS.add(user2DTO);

        List<User> users = userMapper.toUser(userDTOS);
        assert (users.get(0).getUsername().equalsIgnoreCase(userDTOS.get(0).getUsername()));
        assert (users.get(1).getUsername().equalsIgnoreCase(userDTOS.get(1).getUsername()));
        assert (users.get(0).getPassword().equalsIgnoreCase(userDTOS.get(0).getPassword()));
        assert (users.get(1).getPassword().equalsIgnoreCase(userDTOS.get(1).getPassword()));
        assert (users.get(0).getRoles().contains(Role.USER));
        assert (users.get(0).getRoles().contains(Role.USER_MANAGER));
        assert (users.get(1).getRoles().contains(Role.ADMIN));
    }


    @Test
    void roleDTOsToRoles(){
        RoleDTO roleDTO1 = new RoleDTO();
        roleDTO1.setName("USER");
        RoleDTO roleDTO2 = new RoleDTO();
        roleDTO2.setName("USER_MANAGER");
        Set<RoleDTO> roleDTOS = new HashSet<>();
        roleDTOS.add(roleDTO1);
        roleDTOS.add(roleDTO2);
        Set<Role> roles = userMapper.roleDTOsToRoles(roleDTOS);
        assert (roles.size() == 2);
        assert (roles.contains(Role.USER));
        assert (roles.contains(Role.USER_MANAGER));
    }

    @Test
    void rolesToRoleDTOs(){
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);

        RoleDTO userMockedDTO = new RoleDTO();
        userMockedDTO.setName("USER");

        RoleDTO adminMockedDTO = new RoleDTO();
        adminMockedDTO.setName("ADMIN");

        Mockito.when(roleRepository.findByName(Mockito.contains("USER"))).thenReturn(Optional.of(userMockedDTO));
        Mockito.when(roleRepository.findByName(Mockito.contains("ADMIN"))).thenReturn(Optional.of(adminMockedDTO));

        Set<RoleDTO> roleDTOS = userMapper.rolesToRoleDTOs(roles);
        for (RoleDTO roleDTO : roleDTOS) {
            assert (roleDTO.getName().equalsIgnoreCase("USER") ||
                    roleDTO.getName().equalsIgnoreCase("ADMIN"));
        }
        assert (roleDTOS.size() == 2);
    }

    @Test
    void stringSetToRoleSet(){
        Set<String> roleNames = new HashSet<>();
        roleNames.add("USER");
        roleNames.add("ADMIN");
        roleNames.add("XYZ");
        Set<Role> roles = userMapper.stringSetToRoleSet(roleNames);
        assert (roles.size() == 2);
        for (Role role: roles){
            assert (roleNames.contains(role.getName()));
        }

        roleNames.clear();
        roles = userMapper.stringSetToRoleSet(roleNames);
        assert (roles.size() == 1);
        for (Role role: roles){
            assert (role.getName().equalsIgnoreCase("USER"));
        }

        roles = userMapper.stringSetToRoleSet(null);
        assert (roles.size() == 1);
        for (Role role: roles){
            assert (role.getName().equalsIgnoreCase("USER"));
        }
    }

    private User generateUser(String name, String password, Long id){
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        user.setId(id);
        user.setRoles(Set.of(Role.USER));
        return user;
    }

}


