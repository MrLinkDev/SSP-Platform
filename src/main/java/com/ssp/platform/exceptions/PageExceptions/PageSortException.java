package com.ssp.platform.exceptions.PageExceptions;

import com.ssp.platform.response.ErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Класс исключения неверного параметра сортировки элементов страницы
 */
public class PageSortException extends Exception{
    private static final String ERROR_MESSAGE = "Illegal page sorting arg!";

    /**
     * Метод для получения ErrorModel исключения
     *
     * @return ErrorModel
     */
    public static ErrorResponse getErrorModel(){
        return new ErrorResponse(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
    }
}
