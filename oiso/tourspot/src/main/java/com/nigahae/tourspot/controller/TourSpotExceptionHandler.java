package com.nigahae.tourspot.controller;

import com.nigahae.tourspot.exception.InvalidTourSpotIdException;
import com.nigahae.tourspot.exception.TourSpotNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = TourSpotController.class)
public class TourSpotExceptionHandler {

    private static final int TOUR_SPOT_NOT_FOUND_CODE = 4004;
    private static final int INVALID_TOUR_SPOT_ID_CODE = 4000;

    @ExceptionHandler(TourSpotNotFoundException.class)
    public ResponseEntity<TourSpotErrorResponse> handleNotFound() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new TourSpotErrorResponse(
                        TOUR_SPOT_NOT_FOUND_CODE,
                        "관광지를 찾을 수 없습니다.",
                        null
                ));
    }

    @ExceptionHandler(InvalidTourSpotIdException.class)
    public ResponseEntity<TourSpotErrorResponse> handleInvalidSpotId() {
        return ResponseEntity
                .badRequest()
                .body(new TourSpotErrorResponse(
                        INVALID_TOUR_SPOT_ID_CODE,
                        "잘못된 요청입니다.",
                        Map.of("field", "spotId")
                ));
    }

    public record TourSpotErrorResponse(int code, String message, Object data) {
        public TourSpotErrorResponse {
            message = com.nigahae.common.i18n.ApiMessages.translate(message);
        }
    }
}
