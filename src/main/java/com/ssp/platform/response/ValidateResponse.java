package com.ssp.platform.response;

import lombok.Data;

import java.util.List;

@Data
public class ValidateResponse {
	private boolean success;
	private String field;
	private String message;

	public ValidateResponse(boolean success, String field, String message) {
		this.success = success;
		this.field = field;
		this.message = message;
	}

    public ValidateResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        field = null;
    }
}
