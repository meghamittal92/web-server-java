package com.client.calorieserver.service;

import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.exception.EntityAlreadyExistsException;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Service for User objects
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityAlreadyExistsException(User.class, String.format("User with name %s already exists", user.getUsername()));
        }

        final UserDTO userDTO = userRepository.save(userMapper.toUserDTO(user));
        return userMapper.toUser(userDTO);
    }

    @Transactional
    public User replaceById(final Long id, User updatedUser) {

        UserDTO originalUserDTO = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(UserDTO.class, id)
        );

        if (!updatedUser.getUsername().equals(originalUserDTO.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername()))
                throw new EntityAlreadyExistsException(User.class, String.format("User with username %s already exists", updatedUser.getUsername()));
        }

        UserDTO updatedUserDTO = userMapper.toUserDTO(updatedUser);
        updatedUserDTO.setId(originalUserDTO.getId());

        return userMapper.toUser(userRepository.save(updatedUserDTO));
    }


    public User findByUsername(String username) {

        final UserDTO userDTO = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(User.class, username)
        );
        return userMapper.toUser(userDTO);
    }

    public List<User> findAll() {

        return userMapper.toUser((List<UserDTO>) userRepository.findAll());
    }

    @Transactional
    public void deleteById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(User.class, userId)
                );
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        UserDTO userDTO = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Could not find user with username: %s", username)));

        return userMapper.toUser(userDTO);
    }

}
