package com.client.calorieserver.domain.mapper;

import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.db.RoleDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.RoleRepository;
import lombok.Setter;
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


    @Setter
    @Autowired
    private RoleRepository roleRepository;

    @Setter
    @Autowired
    private PasswordEncoder passwordEncoder;

    public abstract UserView toUserView(User user);

    public abstract List<UserView> toUserView(List<User> users);

    @Mapping(target = "roles", qualifiedByName = "stringSetToRoleSet")
    @Mapping(target = "password", qualifiedByName = "passwordToEncodedPassword")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    public abstract User toUser(CreateUserRequest request);

    @Mapping(source = "roles", target = "roleDTOs", qualifiedByName = "rolesToRoleDTOs")
    @Mapping(target = "id", ignore = true)
    public abstract UserDTO toUserDTO(User user);

    @Mapping(source = "roleDTOs", target = "roles", qualifiedByName = "roleDTOsToRoles")
    @Mapping(target = "authorities", ignore = true)
    public abstract User toUser(UserDTO userDTO);

    public abstract List<User> toUser(List<UserDTO> userDTO);

    @Named("roleDTOsToRoles")
    public Set<Role> roleDTOsToRoles(Set<RoleDTO> roleDTOS) {
        HashSet<Role> roles = new HashSet<>();

        for (final RoleDTO roleDTO : roleDTOS) {
            final Role role = Role.get(roleDTO.getName());

            if (role != null)
                roles.add(role);
        }

        return roles;

    }

    @Named("rolesToRoleDTOs")
    public Set<RoleDTO> rolesToRoleDTOs(Set<Role> roles) {
        HashSet<RoleDTO> roleDTOS = new HashSet<>();

        for (final Role role : roles) {
            final Optional<RoleDTO> roleDTOOptional = roleRepository.findByName(role.getName());
            if (roleDTOOptional.isPresent())
                roleDTOS.add(roleDTOOptional.get());

        }

        return roleDTOS;

    }

    //TO DO exception on non existent Role
    @Named("stringSetToRoleSet")
    public Set<Role> stringSetToRoleSet(Set<String> roleSet) {
        HashSet<Role> roles = new HashSet<>();

        if (roleSet != null) {
            for (final String roleString : roleSet) {

                if (Role.get(roleString) != null) {
                    roles.add(Role.get(roleString));
                }

            }
        }
        //by default give the user role to all users
        else {
            roles.add(Role.USER);
        }
        return roles;
    }

    @Named("passwordToEncodedPassword")
    public String passwordToEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

}
