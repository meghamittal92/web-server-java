package com.client.calorieserver.configuration.security;

import com.client.calorieserver.configuration.Constants;
import com.client.calorieserver.domain.model.Role;
import com.client.calorieserver.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JWTAuthorizationFilterTest {

    private final static String token="Bearer token";
    private final static String username="test_user";
    private final static String password="test_password";
    JWTAuthorizationFilter jwtAuthorizationFilter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    JWTUtil jwtUtil;

    @Mock
    UserDetailsService userDetailsService;

    @Mock
    FilterChain chain;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        jwtAuthorizationFilter = new JWTAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Test
    void filterRequest() throws Exception{
        jwtAuthorizationFilter = new JWTAuthorizationFilter(jwtUtil, userDetailsService);
        Mockito.when(request.getHeader(Mockito.anyString())).thenReturn("");
        Mockito.when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        jwtAuthorizationFilter.doFilter(request, response, chain);

        Mockito.verify(request, Mockito.atLeast(1)).getHeader(Mockito.anyString());
        Mockito.verify(jwtUtil, Mockito.times(0))
                .validateJwtToken(Mockito.anyString());

        Mockito.when(request.getHeader(Mockito.anyString())).thenReturn( token);
        Mockito.when(jwtUtil.validateJwtToken(Mockito.anyString())).thenReturn(false);
        jwtAuthorizationFilter.doFilter(request, response, chain);
        Mockito.verify(jwtUtil, Mockito.times(1))
                .validateJwtToken(Mockito.anyString());

        Mockito.when(request.getHeader(Mockito.anyString())).thenReturn( token);
        Mockito.when(jwtUtil.validateJwtToken(Mockito.anyString())).thenReturn(true);
        ArgumentCaptor<String> argumentCaptor =  ArgumentCaptor.forClass(String.class);
        Mockito.when(jwtUtil.getUserNameFromJwtToken(Mockito.anyString())).thenReturn(username);

        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(Constants.SecurityConstants.ROLE_PREFIX + Role.USER.getName()));
        
        User user = createUser();
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(user);
        jwtAuthorizationFilter.doFilter(request, response, chain);
        Mockito.verify(jwtUtil).getUserNameFromJwtToken(argumentCaptor.capture());
        assert argumentCaptor.getValue().equalsIgnoreCase("token");
    }
    
    private User createUser(){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        return user;
    }
}
