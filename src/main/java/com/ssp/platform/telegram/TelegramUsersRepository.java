package com.ssp.platform.telegram;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelegramUsersRepository extends JpaRepository<TelegramUsersEntity, Long> {

    TelegramUsersEntity getTelegramUsersEntityByTempCode(int tempCode);

    TelegramUsersEntity getTelegramUsersEntityByUsername(String username);

    List<TelegramUsersEntity> getTelegramUsersEntitiesByUsernameNotNull();

    boolean existsByTempCode(int tempCode);

    boolean existsByUsername(String username);
}
