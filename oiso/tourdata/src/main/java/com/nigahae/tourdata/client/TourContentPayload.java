package com.nigahae.tourdata.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public record TourContentPayload(
        String contentId,
        String contentTypeId,
        String title,
        String areaCode,
        String sigunguCode,
        String address,
        String mapX,
        String mapY,
        String firstImage,
        String overview,
        String tel,
        String rawPayload
) {

    public static TourContentPayload from(JsonNode item, ObjectMapper objectMapper) {
        String contentId = text(item, "contentid");
        if (contentId == null || contentId.isBlank()) {
            contentId = text(item, "contentId");
        }
        if (contentId == null || contentId.isBlank()) {
            throw new TourApiException("Tour content item has no contentId: " + item);
        }

        return new TourContentPayload(
                contentId,
                firstText(item, "contenttypeid", "contentTypeId"),
                firstText(item, "title", "name"),
                firstText(item, "areacode", "areaCode"),
                firstText(item, "sigungucode", "sigunguCode"),
                address(item),
                firstText(item, "mapx", "mapX", "longitude"),
                firstText(item, "mapy", "mapY", "latitude"),
                firstText(item, "firstimage", "firstImage", "firstimage2", "firstImage2"),
                firstText(item, "overview"),
                firstText(item, "tel"),
                toJson(item, objectMapper)
        );
    }

    public Long contentIdAsLong() {
        return parseLong(contentId, "contentId");
    }

    public Long contentTypeIdAsLong() {
        return parseLong(contentTypeId, "contentTypeId");
    }

    public Double mapXAsDouble() {
        return parseDouble(mapX);
    }

    public Double mapYAsDouble() {
        return parseDouble(mapY);
    }

    private static String firstText(JsonNode node, String... names) {
        for (String name : names) {
            String value = text(node, name);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static String address(JsonNode item) {
        String addr1 = firstText(item, "addr1", "address");
        String addr2 = text(item, "addr2");

        if (addr1 == null || addr1.isBlank()) {
            return blankToNull(addr2);
        }
        if (addr2 == null || addr2.isBlank()) {
            return addr1;
        }
        return addr1 + " " + addr2;
    }

    private static String text(JsonNode node, String name) {
        JsonNode value = node.get(name);
        if (value == null || value.isNull()) {
            return null;
        }
        return value.asText();
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private static String toJson(JsonNode node, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new TourApiException("Failed to serialize tour content item", e);
        }
    }

    private static Long parseLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new TourApiException("Invalid " + fieldName + ": " + value, e);
        }
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
