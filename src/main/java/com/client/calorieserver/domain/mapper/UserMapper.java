package com.client.calorieserver.domain.mapper;

import com.client.calorieserver.domain.dto.CreateUserRequest;
import com.client.calorieserver.domain.dto.UpdateUserRequest;
import com.client.calorieserver.domain.dto.UserView;
import com.client.calorieserver.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


/**
 * Mapper to convert between User model to DTO objects.
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract UserView toUserView(User user);

    public abstract List<UserView> toUserView(List<User> users);

    @Mapping(target = "id", ignore = true)
    public abstract User toUser(CreateUserRequest request);

    @Mapping(target = "id", ignore = true)
    public abstract User toUser(UpdateUserRequest request);


}
