package com.ssp.platform.response;

import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * Модель возврата для ошибки
 */
@Data
@RequiredArgsConstructor
public class ErrorResponse {

    @NonNull
    private HttpStatus httpStatus;

    @NonNull
    private String message;
}
