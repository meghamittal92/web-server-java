package com.client.calorieserver.domain.mapper;



import com.client.calorieserver.domain.dto.db.RoleDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.dto.request.CreateUserRequest;
import com.client.calorieserver.domain.dto.request.RegisterUserRequest;
import com.client.calorieserver.domain.dto.request.UpdateProfileRequest;
import com.client.calorieserver.domain.dto.request.UpdateUserRequest;
import com.client.calorieserver.domain.dto.response.ProfileView;
import com.client.calorieserver.domain.dto.response.UserView;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Component
public class UserMapperNew extends UserMapper {

    @Override
    public UserView toUserView(User user) {
        if ( user == null ) {
            return null;
        }

        UserView userView = new UserView();

        userView.setId( user.getId() );
        userView.setUsername( user.getUsername() );
        userView.setExpectedCaloriesPerDay( user.getExpectedCaloriesPerDay() );
        Set<Role> set = user.getRoles();
        if ( set != null ) {
            userView.setRoles( new HashSet<Role>( set ) );
        }
        userView.setEmail( user.getEmail() );

        return userView;
    }

    @Override
    public ProfileView toProfileView(User user) {
        if ( user == null ) {
            return null;
        }

        ProfileView profileView = new ProfileView();

        profileView.setUsername( user.getUsername() );
        profileView.setExpectedCaloriesPerDay( user.getExpectedCaloriesPerDay() );
        profileView.setEmail( user.getEmail() );

        return profileView;
    }

    @Override
    public List<UserView> toUserView(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserView> list = new ArrayList<UserView>( users.size() );
        for ( User user : users ) {
            list.add( toUserView( user ) );
        }

        return list;
    }

    @Override
    public User toUser(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( request.getUsername() );
        user.setPassword( passwordToEncodedPassword( request.getPassword() ) );
        user.setRoles( stringSetToRoleSet( request.getRoles() ) );
        user.setExpectedCaloriesPerDay( expectedCaloriesMapper( request.getExpectedCaloriesPerDay() ) );
        user.setEmail( request.getEmail() );

        return user;
    }

    @Override
    public User toUser(RegisterUserRequest registerUserRequest) {
        if ( registerUserRequest == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( registerUserRequest.getUsername() );
        user.setPassword( passwordToEncodedPassword( registerUserRequest.getPassword() ) );
        user.setExpectedCaloriesPerDay( expectedCaloriesMapper( registerUserRequest.getExpectedCaloriesPerDay() ) );
        user.setEmail( registerUserRequest.getEmail() );

        afterMappingUser( registerUserRequest, user );

        return user;
    }

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setRoleDTOs( rolesToRoleDTOs( user.getRoles() ) );
        userDTO.setUsername( user.getUsername() );
        userDTO.setPassword( user.getPassword() );
        userDTO.setExpectedCaloriesPerDay( user.getExpectedCaloriesPerDay() );
        userDTO.setEmail( user.getEmail() );

        return userDTO;
    }

    @Override
    public User toUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setRoles( roleDTOsToRoles( userDTO.getRoleDTOs() ) );
        user.setId( userDTO.getId() );
        user.setUsername( userDTO.getUsername() );
        user.setPassword( userDTO.getPassword() );
        user.setExpectedCaloriesPerDay( userDTO.getExpectedCaloriesPerDay() );
        user.setEmail( userDTO.getEmail() );

        return user;
    }

    @Override
    public List<User> toUser(List<UserDTO> userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        List<User> list = new ArrayList<User>( userDTO.size() );
        for ( UserDTO userDTO1 : userDTO ) {
            list.add( toUser( userDTO1 ) );
        }

        return list;
    }

    @Override
    public User updateUser(UpdateProfileRequest updateProfileRequest, User user) {
        if ( updateProfileRequest == null ) {
            return null;
        }

        if ( updateProfileRequest.getExpectedCaloriesPerDay() != null ) {
            user.setExpectedCaloriesPerDay( updateProfileRequest.getExpectedCaloriesPerDay() );
        }
        if ( updateProfileRequest.getEmail() != null ) {
            user.setEmail( updateProfileRequest.getEmail() );
        }

        return user;
    }

    @Override
    public User updateUser(UpdateUserRequest updateUserRequest, User user) {
        if ( updateUserRequest == null ) {
            return null;
        }

        if ( updateUserRequest.getUsername() != null ) {
            user.setUsername( updateUserRequest.getUsername() );
        }
        if ( updateUserRequest.getPassword() != null ) {
            user.setPassword( passwordToEncodedPassword( updateUserRequest.getPassword() ) );
        }
        if ( user.getRoles() != null ) {
            Set<Role> set = stringSetToRoleSet( updateUserRequest.getRoles() );
            if ( set != null ) {
                user.getRoles().clear();
                user.getRoles().addAll( set );
            }
        }
        else {
            Set<Role> set = stringSetToRoleSet( updateUserRequest.getRoles() );
            if ( set != null ) {
                user.setRoles( set );
            }
        }
        if ( updateUserRequest.getExpectedCaloriesPerDay() != null ) {
            user.setExpectedCaloriesPerDay( expectedCaloriesMapper( updateUserRequest.getExpectedCaloriesPerDay() ) );
        }
        if ( updateUserRequest.getEmail() != null ) {
            user.setEmail( updateUserRequest.getEmail() );
        }

        return user;
    }

    @Override
    public UserDTO updateUserDTO(User updatedUser, UserDTO originalUserDTO) {
        if ( updatedUser == null ) {
            return null;
        }

        if ( originalUserDTO.getRoleDTOs() != null ) {
            Set<RoleDTO> set = rolesToRoleDTOs( updatedUser.getRoles() );
            if ( set != null ) {
                originalUserDTO.getRoleDTOs().clear();
                originalUserDTO.getRoleDTOs().addAll( set );
            }
        }
        else {
            Set<RoleDTO> set = rolesToRoleDTOs( updatedUser.getRoles() );
            if ( set != null ) {
                originalUserDTO.setRoleDTOs( set );
            }
        }
        if ( updatedUser.getId() != null ) {
            originalUserDTO.setId( updatedUser.getId() );
        }
        if ( updatedUser.getUsername() != null ) {
            originalUserDTO.setUsername( updatedUser.getUsername() );
        }
        if ( updatedUser.getPassword() != null ) {
            originalUserDTO.setPassword( updatedUser.getPassword() );
        }
        if ( updatedUser.getExpectedCaloriesPerDay() != null ) {
            originalUserDTO.setExpectedCaloriesPerDay( updatedUser.getExpectedCaloriesPerDay() );
        }
        if ( updatedUser.getEmail() != null ) {
            originalUserDTO.setEmail( updatedUser.getEmail() );
        }

        return originalUserDTO;
    }
}
