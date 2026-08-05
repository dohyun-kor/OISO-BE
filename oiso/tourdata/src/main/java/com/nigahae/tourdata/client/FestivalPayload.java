package com.nigahae.tourdata.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public record FestivalPayload(
        TourContentPayload content,
        LocalDate eventStartDate,
        LocalDate eventEndDate
) {

    private static final DateTimeFormatter EVENT_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    public static FestivalPayload from(JsonNode item, ObjectMapper objectMapper) {
        return new FestivalPayload(
                TourContentPayload.from(item, objectMapper),
                parseDate(item, "eventstartdate", "eventStartDate"),
                parseDate(item, "eventenddate", "eventEndDate")
        );
    }

    private static LocalDate parseDate(JsonNode item, String apiFieldName, String fieldName) {
        JsonNode valueNode = item.get(apiFieldName);
        if (valueNode == null || valueNode.isNull()) {
            return null;
        }

        String value = valueNode.asText();
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value, EVENT_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new TourApiException("Invalid " + fieldName + ": " + value, e);
        }
    }
}
