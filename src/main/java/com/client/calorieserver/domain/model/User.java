package com.client.calorieserver.domain.model;

import com.client.calorieserver.configuration.Constants;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * Domain object representing a User.
 */
@Data
public class User implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Set<Role> roles;
    private Integer expectedCaloriesPerDay;
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(Constants.SecurityConstants.ROLE_PREFIX + role.getName()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}