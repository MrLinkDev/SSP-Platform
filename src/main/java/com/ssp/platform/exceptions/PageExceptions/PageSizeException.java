package com.ssp.platform.exceptions.PageExceptions;

import com.ssp.platform.response.ErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Класс исключения неверного размера страницы
 */
public class PageSizeException extends Exception{
    private static final String ERROR_MESSAGE = "Page size must not be less than one!";

    /**
     * Метод для получения ErrorModel исключения
     *
     * @return ErrorModel
     */
    public static ErrorResponse getErrorModel(){
        return new ErrorResponse(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
    }
}
