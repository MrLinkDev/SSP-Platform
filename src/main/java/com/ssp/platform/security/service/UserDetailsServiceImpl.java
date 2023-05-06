package com.ssp.platform.security.service;

import com.ssp.platform.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssp.platform.entity.User;
import com.ssp.platform.repository.UserRepository;

/**
 * Получение информации о пользователе по токену
 * @author Василий Воробьев
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public User loadUserByToken(String token) throws UsernameNotFoundException
    {
        String username = jwtUtils.getUserNameFromJwtToken(token.substring(7));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

}
