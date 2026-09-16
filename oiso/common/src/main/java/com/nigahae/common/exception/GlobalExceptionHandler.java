package com.nigahae.common.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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

import java.util.Arrays;
import java.util.Map;

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
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<CommonError> handleInvalidRequest(Exception exception) {
        if (exception instanceof MissingServletRequestParameterException missing) {
            return invalidField(missing.getParameterName(), "필수 요청 파라미터가 누락되었습니다.", Map.of());
        }
        if (exception instanceof MethodArgumentTypeMismatchException mismatch) {
            return invalidField(mismatch.getName(), "요청 파라미터의 자료형이 올바르지 않습니다.", Map.of());
        }
        return errorResponse(ErrorCode.VALIDATION_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonError> handleUnreadableBody(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof MismatchedInputException mismatch) {
                String field = jsonPath(mismatch);
                Class<?> target = mismatch.getTargetType();
                if (mismatch instanceof InvalidFormatException && target != null && target.isEnum()) {
                    var allowed = Arrays.stream(target.getEnumConstants())
                            .map(value -> ((Enum<?>) value).name()).toList();
                    return invalidField(field, "허용된 값은 " + String.join(", ", allowed) + "입니다.",
                            Map.of("allowedValues", allowed));
                }
                String expected = target == Integer.class || target == Long.class
                        || target == int.class || target == long.class ? "정수"
                        : target == Boolean.class || target == boolean.class ? "true 또는 false"
                        : target == String.class ? "문자열"
                        : target != null && (target.isArray() || java.util.Collection.class.isAssignableFrom(target))
                        ? "배열" : "올바른 JSON 자료형";
                return invalidField(field, expected + " 형식으로 입력해야 합니다.", Map.of());
            }
            cause = cause.getCause();
        }
        return invalidField("request", "요청 본문이 없거나 JSON 문법이 올바르지 않습니다.", Map.of());
    }

    private String jsonPath(JsonMappingException exception) {
        StringBuilder path = new StringBuilder();
        for (JsonMappingException.Reference reference : exception.getPath()) {
            if (reference.getFieldName() != null) {
                if (!path.isEmpty()) path.append('.');
                path.append(reference.getFieldName());
            } else if (reference.getIndex() >= 0) {
                path.append('[').append(reference.getIndex()).append(']');
            }
        }
        return path.isEmpty() ? "request" : path.toString();
    }

    private ResponseEntity<CommonError> invalidField(String field, String message, Map<String, Object> details) {
        Map<String, Object> data = new java.util.HashMap<>(details);
        data.put("field", field);
        return ResponseEntity.badRequest().body(CommonError.from(
                ErrorCode.VALIDATION_ERROR, field + ": " + message, data));
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
