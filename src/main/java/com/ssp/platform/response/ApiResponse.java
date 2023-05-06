package com.ssp.platform.response;

import lombok.Data;

/**
 * Класс формирует json-объект, который возвратится в теле ответа.
 * Ответ типа "успешно-неуспешно" с информационным сообщением.
 */
@Data
public class ApiResponse {

    private boolean success;

    private String message;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}