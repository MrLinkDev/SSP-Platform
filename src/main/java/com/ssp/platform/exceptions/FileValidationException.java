package com.ssp.platform.exceptions;

import com.ssp.platform.response.ValidateResponse;

public class FileValidationException extends Exception {
    private final ValidateResponse response;

    public FileValidationException(ValidateResponse response) {
        super(response.getMessage());
        this.response = response;
    }

    public ValidateResponse getResponse(){
        return response;
    }
}
