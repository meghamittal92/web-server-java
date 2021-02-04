package com.client.calorieserver.configuration.security;

import com.client.calorieserver.domain.model.User;
import com.client.calorieserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
               User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Could not find user with username: %s", username)));

        return new UserDetailsImpl(user);
    }

}
