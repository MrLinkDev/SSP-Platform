package com.ssp.platform.handler;

import com.ssp.platform.response.ValidateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Выводит сообщение в случае если пара логин/пароль неверные при авторизации
 */
@ControllerAdvice
public class WrongLoginPasswordHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(){
        return new ResponseEntity(new ValidateResponse(false, "", "Неверный логин или пароль"), HttpStatus.UNAUTHORIZED);
    }
}
