package com.ssp.platform.telegram;

import com.ssp.platform.response.ApiResponse;

public class TelegramException extends Exception {
    private final ApiResponse response;

    public TelegramException(ApiResponse response) {
        super(response.getMessage());
        this.response = response;
    }

    public ApiResponse getResponse(){
        return response;
    }
}
