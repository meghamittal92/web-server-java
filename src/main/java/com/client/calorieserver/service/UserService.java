package com.client.calorieserver.service;

import com.client.calorieserver.domain.dto.db.CalorieDTO;
import com.client.calorieserver.domain.dto.db.UserDTO;
import com.client.calorieserver.domain.exception.EntityAlreadyExistsException;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.mapper.UserMapper;
import com.client.calorieserver.domain.model.Calorie;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * ervice to provide User CRUD operations.
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

        if (userRepository.existsByEmail(user.getEmail()))
            throw new EntityAlreadyExistsException(User.class, String.format("User with email %s already exists", user.getEmail()));

        final UserDTO userDTO = userRepository.save(userMapper.toUserDTO(user));
        return userMapper.toUser(userDTO);
    }

    @Transactional
    public User replaceById(final Long id, User updatedUser) {

        UserDTO originalUserDTO = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(User.class, id)
        );

        if (!updatedUser.getUsername().equals(originalUserDTO.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername()))
                throw new EntityAlreadyExistsException(User.class, String.format("User with username %s already exists", updatedUser.getUsername()));
        }

        if (!updatedUser.getEmail().equals(originalUserDTO.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail()))
                throw new EntityAlreadyExistsException(User.class, String.format("User with email %s already exists", updatedUser.getEmail()));
        }

        UserDTO updatedUserDTO = userMapper.toUserDTO(updatedUser);
        updatedUserDTO.setId(originalUserDTO.getId());

        return userMapper.toUser(userRepository.save(updatedUserDTO));
    }

    @Transactional
    public User updateById(final Long id, User updatedUser) {

        UserDTO originalUserDTO = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(User.class, id)
        );

        if (!updatedUser.getUsername().equals(originalUserDTO.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername()))
                throw new EntityAlreadyExistsException(User.class, String.format("User with username %s already exists", updatedUser.getUsername()));
        }

        if (!updatedUser.getEmail().equals(originalUserDTO.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail()))
                throw new EntityAlreadyExistsException(User.class, String.format("User with email %s already exists", updatedUser.getEmail()));
        }

        UserDTO updatedUserDTO = userMapper.updateUserDTO(updatedUser, originalUserDTO);

        return userMapper.toUser(userRepository.save(updatedUserDTO));
    }

    public Page<User> findAll(final Pageable pageable) {

        return userRepository.findAll(pageable).map(userMapper::toUser);
    }

    public Page<User> findAll(Specification<UserDTO> spec, Pageable pageable) {

        final Page<UserDTO> userDTOS = userRepository.findAll(spec, pageable);

        return userDTOS.map(userMapper::toUser);
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

    public User findById(final Long userId) {

        final UserDTO userDTO = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, userId)
        );
        return userMapper.toUser(userDTO);
    }
}
