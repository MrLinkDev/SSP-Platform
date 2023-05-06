package com.ssp.platform.exceptions;

import com.ssp.platform.response.*;

public class FileServiceException extends Exception {
    private final ApiResponse response;

    public FileServiceException(ApiResponse response) {
        super(response.getMessage());
        this.response = response;
    }

    public ApiResponse getResponse(){
        return response;
    }
}