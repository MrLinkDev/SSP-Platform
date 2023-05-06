package com.ssp.platform.handler;

import com.ssp.platform.response.ValidateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Выводит сообщение в случае если какой то параметр не предоставлен или неправильный
 * Без этих обработок будет возвращать просто пустоту и 400 ошибку
 */
@ControllerAdvice
public class RequestParameterHandler
{
    /**
     * Когда параметр не предоставлен в RequestBody form-data
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex){
        return new ResponseEntity(new ValidateResponse(false, "", ex.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Когда параметр не предоставлен в Header
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<Object> handleMissingRequestHeaderException(MissingRequestHeaderException ex){
        return new ResponseEntity(new ValidateResponse(false, "", ex.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Когда параметр неправильный
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        return new ResponseEntity(new ValidateResponse(false, "", ex.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

}
