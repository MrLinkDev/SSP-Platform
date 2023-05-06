package com.ssp.platform.telegram.Service;

import com.ssp.platform.entity.User;
import com.ssp.platform.response.ApiResponse;
import com.ssp.platform.service.UserService;
import com.ssp.platform.telegram.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TelegramUsersServiceImpl implements TelegramUsersService {

    private final TelegramUsersRepository telegramUsersRepository;
    private final UserService userService;

    public TelegramUsersServiceImpl(TelegramUsersRepository telegramUsersRepository, UserService userService) {
        this.telegramUsersRepository = telegramUsersRepository;
        this.userService = userService;
    }

    @Override
    public void createChat(long chatId, int securityCode) {
        TelegramUsersEntity telegramUsersEntity = new TelegramUsersEntity();

        if (telegramUsersRepository.existsById(chatId)){
            telegramUsersEntity = telegramUsersRepository.getOne(chatId);
            telegramUsersEntity.setTempCode(securityCode);

            telegramUsersRepository.saveAndFlush(telegramUsersEntity);
        } else {
            telegramUsersEntity.setChatId(chatId);
            telegramUsersEntity.setTempCode(securityCode);

            telegramUsersRepository.save(telegramUsersEntity);
        }
    }

    @Override
    public void connectUserByCode(User user, int securityCode) throws TelegramException {
        if (!telegramUsersRepository.existsByTempCode(securityCode)) throw new TelegramException(new ApiResponse(false, "Неверно введён код"));
        if (telegramUsersRepository.existsByUsername(user.getUsername())) throw new TelegramException(new ApiResponse(false, "Ваши аккаунты уже связаны"));
        TelegramUsersEntity telegramUsersEntity = telegramUsersRepository.getTelegramUsersEntityByTempCode(securityCode);
        telegramUsersEntity.setUsername(user.getUsername());
        telegramUsersEntity.setTempCode(null);
        telegramUsersRepository.saveAndFlush(telegramUsersEntity);

        user.setTgConnected(true);
        userService.update(user);
    }

    @Override
    public Long getChatIdByUser(User user) {
        TelegramUsersEntity telegramUsersEntity = telegramUsersRepository.getTelegramUsersEntityByUsername(user.getUsername());
        return telegramUsersEntity.getChatId();
    }

    @Override
    public String getUsernameByChatId(Long chatId) {
        TelegramUsersEntity entity = telegramUsersRepository.getOne(chatId);
        return entity.getUsername();
    }

    @Override
    public List<TelegramUsersEntity> getAllConnectedUsers() {
        List<TelegramUsersEntity> entities = telegramUsersRepository.getTelegramUsersEntitiesByUsernameNotNull();

        return entities;
    }

    @Override
    public boolean existsByChatId(Long chatId) {
        if (telegramUsersRepository.existsById(chatId) && telegramUsersRepository.getOne(chatId).getUsername() != null) return true;
        else if (telegramUsersRepository.existsById(chatId) && telegramUsersRepository.getOne(chatId).getUsername() == null) return false;
        return telegramUsersRepository.existsById(chatId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return telegramUsersRepository.existsByUsername(username);
    }

    @Override
    public void deleteChat(long chatId) {
        TelegramUsersEntity entity = telegramUsersRepository.getOne(chatId);

        Optional<User> user = userService.findByUsername(entity.getUsername());
        user.get().setTgConnected(false);
        userService.update(user.get());

        telegramUsersRepository.delete(entity);
    }
}
