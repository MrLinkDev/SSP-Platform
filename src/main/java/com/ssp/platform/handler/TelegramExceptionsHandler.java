package com.ssp.platform.handler;

import com.ssp.platform.response.*;
import com.ssp.platform.telegram.TelegramException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class TelegramExceptionsHandler {

    @ExceptionHandler(TelegramException.class)
    protected ResponseEntity<ApiResponse> handleTelegramException(TelegramException exception){
        return new ResponseEntity<>(exception.getResponse(), HttpStatus.BAD_REQUEST);
    }

}
