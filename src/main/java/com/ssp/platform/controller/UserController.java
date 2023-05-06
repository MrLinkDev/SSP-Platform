package com.ssp.platform.controller;

import com.ssp.platform.entity.User;
import com.ssp.platform.logging.Log;
import com.ssp.platform.request.UsersPageRequest;
import com.ssp.platform.response.ApiResponse;
import com.ssp.platform.response.JwtResponse;
import com.ssp.platform.response.ValidateResponse;
import com.ssp.platform.security.jwt.JwtUtils;
import com.ssp.platform.security.service.UserDetailsServiceImpl;
import com.ssp.platform.service.UserService;
import com.ssp.platform.telegram.SSPPlatformBot;
import com.ssp.platform.validate.UserValidate;
import com.ssp.platform.validate.UsersPageValidate;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контроллер для действий с пользователями
 * @author Василий Воробьев
 */
@RestController
public class UserController
{
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserValidate userValidate;
    private final Log log;

    private final SSPPlatformBot sspPlatformBot;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(
            AuthenticationManager authenticationManager, UserService userService, UserDetailsServiceImpl userDetailsService,
            JwtUtils jwtUtils, UserValidate userValidate,
            Log log,
            SSPPlatformBot sspPlatformBot
    )
    {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userValidate = userValidate;
        this.log = log;
        this.sspPlatformBot = sspPlatformBot;
    }

    /**
     * Авторизация пользователя
     * @param username логин
     * @param password пароль
     */
    @GetMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestHeader("username") String username, @RequestHeader("password") String password)
    {
        User ObjUser = new User(username, password);
        userValidate.UserValidateBegin(ObjUser);
        ValidateResponse validateResponse = userValidate.validateLogin();
        if (!validateResponse.isSuccess())
        {
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return new ResponseEntity<>(new JwtResponse(jwt), HttpStatus.OK);
    }

    /**
     * Регистрация для поставщиков
     * @param ObjUser сущность пользователя
     */
    @PostMapping("/registration")
    public ResponseEntity<?> registerFirm(@RequestBody User ObjUser)
    {
        userValidate.UserValidateBegin(ObjUser);
        ValidateResponse validateResponse = userValidate.validateFirmUser();
        if (!validateResponse.isSuccess())
        {
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        try
        {
            userService.save(userValidate.getUser());

            log.info(Log.CONTROLLER_USER, "Поставщик зарегистрирован",
                    ObjUser.getUsername(),
                    ObjUser.getFirmName(),
                    ObjUser.getLastName(),
                    ObjUser.getPatronymic(),
                    ObjUser.getFirmName(),
                    ObjUser.getDescription(),
                    ObjUser.getAddress(),
                    ObjUser.getActivity(),
                    ObjUser.getTechnology(),
                    ObjUser.getInn(),
                    ObjUser.getTelephone(),
                    ObjUser.getEmail());

            return new ResponseEntity<>(new ApiResponse(true,
                    "Вы успешно зарегестрировались, ожидайте аккредитации от сотрудника"), HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Создание нового сотрудника другим сотрудником
     * @param token токен авторизации
     * @param ObjUser сущность пользователя
     */
    @PostMapping("/regemployee")
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<?> registerEmployee(@RequestHeader("Authorization") String token, @RequestBody User ObjUser)
    {
        userValidate.UserValidateBegin(ObjUser);
        ValidateResponse validateResponse = userValidate.validateEmployeeUser();
        if (!validateResponse.isSuccess())
        {
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        try
        {
            userService.save(userValidate.getUser());

            log.info(userDetailsService.loadUserByToken(token), Log.CONTROLLER_USER, "Поставщик зарегистрирован",
                    ObjUser.getUsername(),
                    ObjUser.getFirmName(),
                    ObjUser.getLastName(),
                    ObjUser.getPatronymic(),
                    ObjUser.getFirmName(),
                    ObjUser.getDescription(),
                    ObjUser.getAddress(),
                    ObjUser.getActivity(),
                    ObjUser.getTechnology(),
                    ObjUser.getInn(),
                    ObjUser.getTelephone(),
                    ObjUser.getEmail());

            return new ResponseEntity<>(new ApiResponse(true, "Сотрудник успешно зарегестрирован!"), HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Получение списков пользователей
     * @param type тип пользователей по роли
     * @param requestPage номер страницы
     * @param numberOfElements колличество элементов на странице
     */
    @GetMapping(value = "/users/{type}")
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<Object> getUsers(@PathVariable(name = "type") String type,
                                           @RequestParam(value = "requestPage", required = false) Integer requestPage,
                                           @RequestParam(value = "numberOfElements", required = false) Integer numberOfElements)
    {
        UsersPageRequest usersPageRequest = new UsersPageRequest(requestPage, numberOfElements, type);
        UsersPageValidate usersPageValidate = new UsersPageValidate(usersPageRequest);
        ValidateResponse validateResponse = usersPageValidate.validateUsersPage();

        if (!validateResponse.isSuccess())
        {
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        UsersPageRequest validUsersPageRequest = usersPageValidate.getUsersPageRequest();

        Pageable pageable;
        if (type.equals("firm"))
        {
            pageable = PageRequest.of(validUsersPageRequest.getRequestPage(),
                    validUsersPageRequest.getNumberOfElements(), Sort.by("firmName").descending());
        }
        //else if (type.equals("employee"))
        else
        {
            pageable = PageRequest.of(validUsersPageRequest.getRequestPage(),
                    validUsersPageRequest.getNumberOfElements(), Sort.by("lastName").descending());
        }

        Page<User> searchResult = userService.findAllByRole(pageable, validUsersPageRequest.getType());

        return new ResponseEntity<>(searchResult, HttpStatus.OK);
    }

    /**
     * Получение информации по одному пользователю
     * @param username имя пользователя
     * @param token токен авторизации
     */
    @GetMapping(value = "/user/{username}")
    @PreAuthorize("hasAuthority('employee') or hasAuthority('firm')")
    public ResponseEntity<Object> getUser(@PathVariable(name = "username") String username, @RequestHeader("Authorization") String token)
    {
        ValidateResponse validateResponse = userValidate.validateUsernameLogin(username);

        if (!validateResponse.isSuccess())
        {
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        User user = userDetailsService.loadUserByToken(token);
        if (user.getRole().equals("firm") && !username.equals(user.getUsername()))
        {
            return new ResponseEntity<>(new ApiResponse(false, "Вы можете смотреть информацию только по своему аккаунту"), HttpStatus.FORBIDDEN);
        }

        Optional<User> searchResult = userService.findByUsername(username);

        if (searchResult.isPresent())
        {
            return new ResponseEntity<>(searchResult.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse(false, "Пользователь не найден"), HttpStatus.NOT_FOUND);
    }

    /**
     * Изменение информации о пользователе
     * @param objUser сущность пользователя
     * @param token токен авторизации
     */
    @PutMapping(value = "/user")
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<Object> editUser(@RequestBody User objUser, @RequestHeader("Authorization") String token)
    {
        ValidateResponse validateResponse = userValidate.validateUsernameLogin(objUser.getUsername());

        if (!validateResponse.isSuccess())
        {
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        Optional<User> searchResult = userService.findByUsername(objUser.getUsername());

        if (!searchResult.isPresent())
        {
            return new ResponseEntity<>(new ApiResponse(false, "Пользователь не найден"), HttpStatus.NOT_FOUND);
        }

        User oldUser = searchResult.get();

        Object[] was = {
                oldUser.getUsername(),
                oldUser.getFirmName(),
                oldUser.getLastName(),
                oldUser.getPatronymic(),
                oldUser.getFirmName(),
                oldUser.getDescription(),
                oldUser.getAddress(),
                oldUser.getActivity(),
                oldUser.getTechnology(),
                oldUser.getInn(),
                oldUser.getTelephone(),
                oldUser.getEmail(),
                oldUser.getStatus()};

        userValidate.UserValidateBegin(oldUser);

        if (oldUser.getRole().equals("firm"))
        {
            validateResponse = userValidate.validateEditFirmUser(objUser);
        }
        else
        {
            validateResponse = userValidate.validateEditEmployeeUser(objUser);
            /*
            User user = userDetailsService.loadUserByToken(token);
            if (oldUser.getUsername().equals(user.getUsername()))
                validateResponse = userValidate.validateEditEmployeeUser(objUser);
            else return new ResponseEntity<>(new ApiResponse(false, "Вы не можете менять" +
                    " информацию по аккаунту другого сотрудника"), HttpStatus.FORBIDDEN);
             */
        }

        if (!validateResponse.isSuccess())
        {
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        User validUser = userValidate.getUser();

        try
        {
            Object[] became = {
                    validUser.getUsername(),
                    validUser.getFirmName(),
                    validUser.getLastName(),
                    validUser.getPatronymic(),
                    validUser.getFirmName(),
                    validUser.getDescription(),
                    validUser.getAddress(),
                    validUser.getActivity(),
                    validUser.getTechnology(),
                    validUser.getInn(),
                    validUser.getTelephone(),
                    validUser.getEmail(),
                    validUser.getStatus()};

            if (was[was.length - 1].equals("NotApproved") && validUser.getStatus().equals("Approved")){
                logger.info("status");
                sspPlatformBot.notifyAboutStatusChanges(validUser);
            }

            log.info(userDetailsService.loadUserByToken(token), Log.CONTROLLER_USER, "Пользователь изменён", was, became);

            return new ResponseEntity<>(userService.update(validUser), HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

