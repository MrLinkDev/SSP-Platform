package com.ssp.platform.service.impl;

import com.ssp.platform.entity.User;
import com.ssp.platform.property.UserCreateProperty;
import com.ssp.platform.repository.UserRepository;
import com.ssp.platform.response.ValidateResponse;
import com.ssp.platform.service.UserService;
import com.ssp.platform.validate.UserValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями
 * @author Василий Воробьев
 */
@Service
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;
    private final UserValidate userValidate;
    private final PasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserCreateProperty userCreateProperty, UserRepository userRepository,
                           UserValidate userValidate, PasswordEncoder encoder)
    {
        this.userRepository = userRepository;
        this.userValidate = userValidate;
        this.encoder = encoder;

        User newUser = new User(userCreateProperty.getUsername(), userCreateProperty.getPassword());
        newUser.setFirstName(userCreateProperty.getFirstName());
        newUser.setLastName(userCreateProperty.getLastName());
        newUser.setPatronymic(userCreateProperty.getPatronymic());
        newUser.setTgConnected(false);

        createUserFromProperties(newUser);
    }

    /**
     * Сохранение нового пользователя
     * @param user пользователь
     */
    @Override
    public User save(User user)
    {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setTgConnected(false);
        return userRepository.save(user);
    }

    /**
     * Обновление данных о пользователе
     * @param user пользователь
     */
    @Override
    public User update(User user)
    {
        return userRepository.save(user);
    }

    /**
     * Получение списка пользователей по роли с пагинацией
     * @param pageable данные по пагинации
     * @param role роль пользователей
     */
    @Override
    public Page<User> findAllByRole(Pageable pageable, String role)
    {
        return userRepository.findAllByRole(pageable, role);
    }

    @Override
    public Optional<User> findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByRoleAndStatus(String role, String status)
    {
        return userRepository.findByRoleAndStatus(role, status);
    }

    /**
     * Создание пользователя сотрудника из данных в application.properties
     * @param newUser пользователь
     */
    private void createUserFromProperties(User newUser)
    {
        if(userRepository.existsByUsername(newUser.getUsername())) return;

        userValidate.UserValidateBegin(newUser);
        ValidateResponse validateResponse = userValidate.validateEmployeeUser();
        if (!validateResponse.isSuccess())
        {
            //TODO log.warning("Данные сотрудника в конфигурации заполнены некорректно:\n  " + validateResponse.getMessage());
            return;
        }

        User validUser = userValidate.getUser();
        validUser.setPassword(encoder.encode(validUser.getPassword()));

        try
        {
            userRepository.save(validUser);
        }
        catch (Exception e)
        {
            //TODO log.warning("Сохранить данные сотрудника из конфигурации не удалось:\n  " + e.getMessage());
        }
    }
}
