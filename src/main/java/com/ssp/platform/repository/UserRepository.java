package com.ssp.platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ssp.platform.entity.User;

/**
 * Репозиторий для работы с сущностью пользователя
 * @author Василий Воробьев
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);

    boolean existsByInn(String tIN);

    Page<User> findAllByRole(Pageable pageable, String role);

    List<User> findByRoleAndStatus(String role, String status);
}
