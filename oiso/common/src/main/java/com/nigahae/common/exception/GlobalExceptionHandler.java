package com.nigahae.common.exception;

import com.nigahae.common.response.ApiResponse;
import com.nigahae.common.response.ErrorDetail;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomCommonException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomCommonException(
            CustomCommonException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorDetail>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        String field = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    return path.substring(path.lastIndexOf('.') + 1);
                })
                .orElse(null);
        return invalidRequest(field);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<ErrorDetail>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        return invalidRequest(exception.getName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorDetail>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception
    ) {
        String field = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField())
                .orElse(null);
        return invalidRequest(field);
    }

    private ResponseEntity<ApiResponse<ErrorDetail>> invalidRequest(String field) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        new ErrorDetail(field)
                ));
    }
}
