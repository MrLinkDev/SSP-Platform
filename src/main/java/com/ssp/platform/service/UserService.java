package com.ssp.platform.service;

import com.ssp.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с сущностью List
 */
public interface UserService
{
    User save(User user);

    Page<User> findAllByRole(Pageable pageable, String role);

    Optional<User> findByUsername(String username);

    List<User> findByRoleAndStatus(String role, String status);

    public User update(User user);

}
