package com.ssp.platform.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssp.platform.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Конфигурация стандартного пользователя Spring security
 * Отключаем ненужные атрибуты такие как isAccountNonExpired (истек ли срок аккаунта) и тд
 * authorities представлен ролью пользователя - firm, employee
 * @author Василий Воробьев
 */
public class UserDetailsImpl implements UserDetails
{
    private final String username;
    private final String email;
    @JsonIgnore
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user)
    {
        HashSet<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new UserDetailsImpl(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    public String getEmail() { return email; }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(username, user.getUsername());
    }
}
