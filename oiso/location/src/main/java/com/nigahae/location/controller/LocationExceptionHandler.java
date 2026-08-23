package com.nigahae.location.controller;

import com.nigahae.common.error.CommonError;
import com.nigahae.location.client.MapProviderException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(assignableTypes = LocationController.class)
public class LocationExceptionHandler {
    @ExceptionHandler(MapProviderException.class)
    ResponseEntity<CommonError> handleProviderException(MapProviderException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new CommonError(5020, exception.getMessage(), null));
    }
}
