package com.ssp.platform.handler;

import com.ssp.platform.exceptions.*;
import com.ssp.platform.response.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@ControllerAdvice
public class FileExceptionsHandler {

    @ExceptionHandler(FileValidationException.class)
    protected ResponseEntity<ValidateResponse> handleFileValidationException(FileValidationException exception){
        return new ResponseEntity<>(exception.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    protected ResponseEntity<ApiResponse> handleNotFoundException(){
        return new ResponseEntity<>(new ApiResponse(false, "Файл удалён"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileServiceException.class)
    protected ResponseEntity<ApiResponse> handleNotFoundException(FileServiceException exception){
        return new ResponseEntity<>(exception.getResponse(), HttpStatus.BAD_REQUEST);
    }
}
