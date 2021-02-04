package com.client.calorieserver.service;

import com.client.calorieserver.domain.exception.EntityAlreadyExistsException;
import com.client.calorieserver.domain.exception.EntityNotFoundException;
import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Service for User objects
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityAlreadyExistsException(User.class, String.format("User with name %s already exists", user.getUsername()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public User replaceById(final Long id, User updatedUser) {

        User originalUser = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(User.class, id)
        );

        if (!updatedUser.getUsername().equals(originalUser.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername()))
                throw new EntityAlreadyExistsException(User.class, String.format("User with username %s already exists", updatedUser.getUsername()));
        }

        updatedUser.setId(originalUser.getId());

        return userRepository.save(updatedUser);
    }


    public User findByUsername(String username) {

        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(User.class, username)
        );
    }

    public List<User> findAll() {

        return (List<User>) userRepository.findAll();
    }

    @Transactional
    public void deleteById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(User.class, userId)
                );
        userRepository.deleteById(userId);
    }

}
