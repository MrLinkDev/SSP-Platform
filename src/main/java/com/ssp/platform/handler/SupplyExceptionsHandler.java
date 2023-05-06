package com.ssp.platform.handler;

import com.ssp.platform.exceptions.*;
import com.ssp.platform.response.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class SupplyExceptionsHandler {

    @ExceptionHandler(SupplyValidationException.class)
    protected ResponseEntity<ValidateResponse> handleSupplyValidationException(SupplyValidationException exception){
        return new ResponseEntity<>(exception.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SupplyServiceException.class)
    protected ResponseEntity<ApiResponse> handleSupplyServiceException(SupplyServiceException exception){
        return new ResponseEntity<>(exception.getResponse(), HttpStatus.BAD_REQUEST);
    }
}
