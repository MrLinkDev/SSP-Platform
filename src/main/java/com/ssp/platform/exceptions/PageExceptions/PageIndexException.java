package com.ssp.platform.exceptions.PageExceptions;

import com.ssp.platform.response.ErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Класс исключения неверного индекса страницы
 */
public class PageIndexException extends Exception{
    private static final String ERROR_MESSAGE = "Page index must not be less than zero!";

    /**
     * Метод для получения ErrorModel исключения
     *
     * @return ErrorModel
     */
    public static ErrorResponse getErrorModel(){
        return new ErrorResponse(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
    }
}
