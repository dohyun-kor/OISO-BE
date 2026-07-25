package com.nigahae.tourdata.client;

import com.fasterxml.jackson.databind.JsonNode;

public record AccommodationRoomPayload(
        String roomCode,
        String roomTitle,
        String roomSizePyeong,
        String roomSizeSquareMeter,
        String roomCount,
        String baseCount,
        String maxCount,
        String offseasonWeekdayMinFee,
        String offseasonWeekendMinFee,
        String peakSeasonWeekdayMinFee,
        String peakSeasonWeekendMinFee,
        String roomIntro,
        String hasBathFacility,
        String hasBath,
        String hasHomeTheater,
        String hasAirCondition,
        String hasTv,
        String hasPc,
        String hasCable,
        String hasInternet,
        String hasRefrigerator,
        String hasToiletries,
        String hasSofa,
        String hasCook,
        String hasTable,
        String hasHairdryer
) {

    public static AccommodationRoomPayload from(JsonNode item) {
        return new AccommodationRoomPayload(
                text(item, "roomcode"),
                text(item, "roomtitle"),
                text(item, "roomsize1"),
                text(item, "roomsize2"),
                text(item, "roomcount"),
                text(item, "roombasecount"),
                text(item, "roommaxcount"),
                text(item, "roomoffseasonminfee1"),
                text(item, "roomoffseasonminfee2"),
                text(item, "roompeakseasonminfee1"),
                text(item, "roompeakseasonminfee2"),
                text(item, "roomintro"),
                text(item, "roombathfacility"),
                text(item, "roombath"),
                text(item, "roomhometheater"),
                text(item, "roomaircondition"),
                text(item, "roomtv"),
                text(item, "roompc"),
                text(item, "roomcable"),
                text(item, "roominternet"),
                text(item, "roomrefrigerator"),
                text(item, "roomtoiletries"),
                text(item, "roomsofa"),
                text(item, "roomcook"),
                text(item, "roomtable"),
                text(item, "roomhairdryer")
        );
    }

    private static String text(JsonNode node, String name) {
        JsonNode value = node.get(name);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText();
        if (text == null || text.isBlank()) {
            return null;
        }
        return text;
    }
}
