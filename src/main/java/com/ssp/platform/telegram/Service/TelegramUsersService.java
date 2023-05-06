package com.ssp.platform.telegram.Service;

import com.ssp.platform.entity.User;
import com.ssp.platform.telegram.*;

import java.util.List;

public interface TelegramUsersService {

    void createChat(long chatId, int securityCode);

    void connectUserByCode(User user, int securityCode) throws TelegramException;

    Long getChatIdByUser(User user);

    String getUsernameByChatId(Long chatId);

    List<TelegramUsersEntity> getAllConnectedUsers();

    boolean existsByChatId(Long chatId);

    boolean existsByUsername(String username);

    void deleteChat(long chatId);
}
