package com.nigahae.common.exception;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.error.CommonError;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BindException.class)
    public ResponseEntity<CommonError> handleBindException(BindException exception) {
        String message = exception.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(this::hasText)
                .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

        return errorResponse(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<CommonError> handleMethodValidationException(
            HandlerMethodValidationException exception
    ) {
        String message = exception.getAllErrors().stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(this::hasText)
                .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

        return errorResponse(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonError> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .filter(this::hasText)
                .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

        return errorResponse(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<CommonError> handleInvalidRequest(Exception exception) {
        return errorResponse(ErrorCode.VALIDATION_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonError> handleCustomException(CustomException exception) {
        return ResponseEntity
                .status(exception.getErrorCode().getHttpStatus())
                .body(CommonError.from(
                        exception.getErrorCode(), exception.getMessage(), exception.getData()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonError> handleUnexpectedException(Exception exception) {
        log.error("Unhandled exception", exception);
        return errorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<CommonError> errorResponse(ErrorCode errorCode) {
        return errorResponse(errorCode, errorCode.getMessage());
    }

    private ResponseEntity<CommonError> errorResponse(ErrorCode errorCode, String message) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonError.from(errorCode, message));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
