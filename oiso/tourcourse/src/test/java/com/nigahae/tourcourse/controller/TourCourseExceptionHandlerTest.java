package com.nigahae.tourcourse.controller;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;

import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TourCourseExceptionHandlerTest {
    private final TourCourseExceptionHandler handler = new TourCourseExceptionHandler();

    @ParameterizedTest
    @ValueSource(strings = {"ux_tour_course_item_day_order", "UK_TOURCOURSEITEM_DAY_ORDER", "UK_TOURCOURSEITEM_ORDER"})
    void identifiesOrderConflictWithoutExposingDatabaseDetails(String constraint) {
        var response = handler.handlePersistenceFailure(new DataIntegrityViolationException(
                "private database information", violation(constraint)));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().code()).isEqualTo(4009);
        assertThat(((Map<?, ?>) response.getBody().data()).get("reason")).isEqualTo("COURSE_ITEM_ORDER_CONFLICT");
        assertThat(((Map<?, ?>) response.getBody().data()).get("field")).isEqualTo("items.itemOrder");
        assertThat(response.getBody().toString()).doesNotContain("private", constraint);
        assertThat(((Map<?, ?>) response.getBody().data()).get("errorId").toString())
                .matches("[0-9a-f-]{36}");
    }

    @Test
    void identifiesConstraintWrappedByTransactionCommitFailure() {
        var response = handler.handlePersistenceFailure(new TransactionSystemException(
                "commit failed", new RuntimeException(violation("FK_TOURCOURSEITEM_NEARBYPLACE"))));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(((Map<?, ?>) response.getBody().data()).get("reason")).isEqualTo("COURSE_PLACE_REFERENCE_CONFLICT");
        assertThat(((Map<?, ?>) response.getBody().data()).get("field")).isEqualTo("items.nearbyPlaceId");
    }

    @Test
    void identifiesDuplicatePlace() {
        var response = handler.handlePersistenceFailure(violation("UK_TOURCOURSEITEM_NEARBYPLACE"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(((Map<?, ?>) response.getBody().data()).get("reason")).isEqualTo("COURSE_PLACE_CONFLICT");
    }

    @Test
    void unknownConstraintRemainsServerErrorWithTraceId() {
        var response = handler.handlePersistenceFailure(violation("PRIVATE_UNKNOWN_CONSTRAINT"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().code()).isEqualTo(5001);
        assertThat(((Map<?, ?>) response.getBody().data()).get("reason")).isEqualTo("COURSE_PERSISTENCE_FAILURE");
        assertThat(response.getBody().toString()).doesNotContain("PRIVATE", "private");
    }

    private ConstraintViolationException violation(String name) {
        return new ConstraintViolationException("private SQL", new SQLException("private values"), name);
    }
}
