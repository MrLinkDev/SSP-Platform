package com.ssp.platform.exceptions;

import com.ssp.platform.response.ApiResponse;

public class SupplyServiceException extends Exception {
    private final ApiResponse response;

    public SupplyServiceException(ApiResponse response) {
        super(response.getMessage());
        this.response = response;
    }

    public ApiResponse getResponse(){
        return response;
    }
}
