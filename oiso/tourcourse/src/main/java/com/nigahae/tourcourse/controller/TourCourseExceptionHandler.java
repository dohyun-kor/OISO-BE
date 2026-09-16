package com.nigahae.tourcourse.controller;

import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.error.CommonError;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = TourCourseController.class)
public class TourCourseExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(TourCourseExceptionHandler.class);

    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class,
            TransactionSystemException.class})
    public ResponseEntity<CommonError> handlePersistenceFailure(Exception exception) {
        String errorId = UUID.randomUUID().toString();
        log.error("Tour course persistence failure, errorId={}", errorId, exception);
        String constraint = "";
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation && violation.getConstraintName() != null) {
                constraint = violation.getConstraintName().replace("\"", "").toUpperCase(Locale.ROOT);
                break;
            }
        }
        String reason;
        String field;
        String message;
        switch (constraint) {
            case "UX_TOUR_COURSE_ITEM_DAY_ORDER", "UK_TOURCOURSEITEM_DAY_ORDER", "UK_TOURCOURSEITEM_ORDER" -> {
                reason = "COURSE_ITEM_ORDER_CONFLICT";
                field = "items.itemOrder";
                message = "코스 저장 중 같은 일차의 장소 순서가 충돌했습니다. 코스 상세를 다시 조회한 뒤 수정해 주세요.";
            }
            case "UK_TOURCOURSEITEM_TOURSPOT", "UK_TOURCOURSEITEM_NEARBYPLACE" -> {
                reason = "COURSE_PLACE_CONFLICT";
                field = "items";
                message = "코스 저장 중 동일한 장소가 중복되었습니다. 기존 장소의 itemId와 중복 추가 여부를 확인해 주세요.";
            }
            case "FK_TOURCOURSEITEM_TOURSPOT", "FK_TOURCOURSEITEM_NEARBYPLACE" -> {
                reason = "COURSE_PLACE_REFERENCE_CONFLICT";
                field = constraint.endsWith("NEARBYPLACE") ? "items.nearbyPlaceId" : "items.spotId";
                message = "코스 장소 참조 제약으로 저장 또는 삭제할 수 없습니다. 장소가 존재하는지와 다른 데이터에서 사용 중인지 확인해 주세요.";
            }
            default -> {
                return ResponseEntity.internalServerError().body(CommonError.from(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "관광 코스 데이터를 DB에 반영하지 못했습니다. 문제가 계속되면 errorId와 함께 문의해 주세요.",
                        Map.of("reason", "COURSE_PERSISTENCE_FAILURE", "errorId", errorId)));
            }
        }
        return ResponseEntity.status(ErrorCode.TOUR_COURSE_SAVE_CONFLICT.getHttpStatus())
                .body(CommonError.from(ErrorCode.TOUR_COURSE_SAVE_CONFLICT, message,
                        Map.of("reason", reason, "field", field, "errorId", errorId)));
    }
}
