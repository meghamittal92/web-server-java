package com.client.calorieserver.domain.mapper;

import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.RoleRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Mapper to convert between User model to DTO objects.
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper {


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public abstract UserView toUserView(User user);

    public abstract List<UserView> toUserView(List<User> users);

    @Mapping(target = "roles", qualifiedByName = "stringSetToRoleSet")
    @Mapping(target = "password", qualifiedByName = "passwordToEncodedPassword")
    @Mapping(target = "id", ignore = true)
    public abstract User toUser(CreateUserRequest request);


    @Named("stringSetToRoleSet")
    public Set<Role> stringSetToRoleSet(Set<String> roleSet) {
        HashSet<Role> roles = new HashSet<>();

        if (roleSet != null) {
            for (final String roleString : roleSet) {
                final Optional<Role> role = roleRepository.findByName(roleString);

                if (role.isPresent()) {
                    roles.add(role.get());

                }
            }
        }
        //by default give the user role to all users
        else {
            roles.add(roleRepository.findByName(Role.USER).get());
        }
        return roles;
    }

    @Named("passwordToEncodedPassword")
    public String passwordToEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

}
