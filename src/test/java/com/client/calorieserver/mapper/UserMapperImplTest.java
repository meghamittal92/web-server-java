package com.client.calorieserver.mapper;

import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.mapper.UserMapperImpl;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.RoleRepository;
import com.client.calorieserver.repository.UserRepository;
import com.client.calorieserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

public class UserMapperImplTest {

    private UserMapperImpl userMapper;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;

    @BeforeEach
    void initController(){
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

    private User generateUser(String name, String password, Long id){
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        user.setId(id);
        user.setRoles(Set.of(Role.USER));
        return user;
    }

}


