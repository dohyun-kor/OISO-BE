package com.nigahae.location.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.nigahae.location.config.KakaoMapProperties;
import com.nigahae.location.domain.RouteTransportType;
import com.nigahae.location.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class KakaoMapProvider implements MapProvider {
    private final RestClient mapRestClient;
    private final RestClient mobilityRestClient;
    private final KakaoMapProperties properties;

    public KakaoMapProvider(
            @Qualifier("kakaoMapRestClient") RestClient kakaoMapRestClient,
            @Qualifier("kakaoMobilityRestClient") RestClient kakaoMobilityRestClient,
            KakaoMapProperties properties
    ) {
        this.mapRestClient = kakaoMapRestClient;
        this.mobilityRestClient = kakaoMobilityRestClient;
        this.properties = properties;
    }

    @Override
    public RouteProviderResult calculateRoute(CoordinateRequest origin, CoordinateRequest destination,
                                               RouteTransportType type) {
        if (type == RouteTransportType.CAR) {
            return calculateCarRoute(origin, destination);
        }
        JsonNode root = get(routePath(type), builder -> builder
                .queryParam("start_x", origin.longitude()).queryParam("start_y", origin.latitude())
                .queryParam("s_name", name(origin.name(), "출발"))
                .queryParam("end_x", destination.longitude()).queryParam("end_y", destination.latitude())
                .queryParam("e_name", name(destination.name(), "도착"))
                .queryParam("input_coord", "WGS84").queryParam("output_coord", "WGS84"));
        if (type == RouteTransportType.PUBLIC_TRANSIT) {
            String status = root.path("status").asText();
            if (!"OK".equals(status) || root.path("routes").isEmpty())
                throw new MapProviderException("경로를 찾을 수 없습니다. status=" + status);
            JsonNode route = root.path("routes").get(0).path("properties");
            return new RouteProviderResult(route.path("totalDistance").asInt(), route.path("totalTime").asInt(),
                    root.path("properties").path("landingURL").asText());
        }
        JsonNode route = root.path("route").path("properties");
        if (!route.has("totalDistance")) throw new MapProviderException("경로를 찾을 수 없습니다.");
        return new RouteProviderResult(route.path("totalDistance").asInt(), route.path("totalTime").asInt(),
                route.path("landingUrl").asText());
    }

    private RouteProviderResult calculateCarRoute(CoordinateRequest origin, CoordinateRequest destination) {
        validateKey();
        try {
            JsonNode root = mobilityRestClient.get().uri(builder -> builder
                    .path("/v1/directions")
                    .queryParam("origin", coordinate(origin))
                    .queryParam("destination", coordinate(destination))
                    .queryParam("priority", "RECOMMEND")
                    .queryParam("summary", true)
                    .build()).retrieve().body(JsonNode.class);
            if (root == null || root.path("routes").isEmpty()) {
                throw new MapProviderException("자동차 경로를 찾을 수 없습니다.");
            }
            JsonNode route = root.path("routes").get(0);
            int resultCode = route.path("result_code").asInt(-1);
            if (resultCode != 0) {
                throw new MapProviderException("자동차 경로를 찾을 수 없습니다. resultCode=" + resultCode
                        + ", message=" + route.path("result_msg").asText());
            }
            JsonNode summary = route.path("summary");
            return new RouteProviderResult(summary.path("distance").asInt(), summary.path("duration").asInt(),
                    navigationUrl(destination));
        } catch (RestClientException exception) {
            throw new MapProviderException("Kakao Mobility 자동차 길찾기 API 호출에 실패했습니다.", exception);
        }
    }

    @Override
    public GeocodingResponse geocode(String address, int page, int size) {
        JsonNode root = get("/v2/local/search/address.json", builder -> builder
                .queryParam("query", address).queryParam("page", page).queryParam("size", size));
        List<AddressCandidateResponse> results = new ArrayList<>();
        root.path("documents").forEach(node -> {
            JsonNode lot = node.path("address");
            JsonNode road = node.path("road_address");
            results.add(new AddressCandidateResponse(text(lot, "address_name"), text(road, "address_name"),
                    text(lot, "region_1depth_name"), text(lot, "region_2depth_name"),
                    text(lot, "region_3depth_name"), node.path("x").asDouble(), node.path("y").asDouble()));
        });
        return new GeocodingResponse(address, root.path("meta").path("total_count").asInt(), results);
    }

    @Override
    public ReverseGeocodingResponse reverseGeocode(double longitude, double latitude) {
        JsonNode documents = get("/v2/local/geo/coord2address.json", builder -> builder
                .queryParam("x", longitude).queryParam("y", latitude).queryParam("input_coord", "WGS84"))
                .path("documents");
        if (documents.isEmpty()) throw new MapProviderException("좌표에 해당하는 주소를 찾을 수 없습니다.");
        JsonNode lot = documents.get(0).path("address");
        JsonNode road = documents.get(0).path("road_address");
        return new ReverseGeocodingResponse(longitude, latitude, text(lot, "address_name"),
                text(road, "address_name"), text(lot, "region_1depth_name"),
                text(lot, "region_2depth_name"), text(lot, "region_3depth_name"));
    }

    @Override
    public List<RegionResponse> findRegions(double longitude, double latitude) {
        JsonNode documents = get("/v2/local/geo/coord2regioncode.json", builder -> builder
                .queryParam("x", longitude).queryParam("y", latitude).queryParam("input_coord", "WGS84"))
                .path("documents");
        List<RegionResponse> result = new ArrayList<>();
        documents.forEach(node -> result.add(new RegionResponse(text(node, "region_type"),
                text(node, "address_name"), text(node, "region_1depth_name"),
                text(node, "region_2depth_name"), text(node, "region_3depth_name"),
                text(node, "region_4depth_name"), text(node, "code"))));
        return result;
    }

    private JsonNode get(String path, Function<UriBuilder, UriBuilder> query) {
        validateKey();
        try {
            JsonNode value = mapRestClient.get().uri(builder -> query.apply(builder.path(path)).build())
                    .retrieve().body(JsonNode.class);
            if (value == null) throw new MapProviderException("Kakao Map API가 빈 응답을 반환했습니다.");
            return value;
        } catch (RestClientException exception) {
            throw new MapProviderException("Kakao Map API 호출에 실패했습니다.", exception);
        }
    }

    private void validateKey() {
        if (properties.getRestApiKey() == null || properties.getRestApiKey().isBlank())
            throw new MapProviderException("kakao.map.rest-api-key 설정이 필요합니다.");
    }
    private String routePath(RouteTransportType type) {
        return switch (type) {
            case WALK -> "/v2/routing/walk";
            case BICYCLE -> "/v2/routing/bicycle";
            case PUBLIC_TRANSIT -> "/v2/routing/publictraffic";
            case CAR -> throw new IllegalArgumentException("CAR uses Kakao Mobility API");
        };
    }
    private String coordinate(CoordinateRequest point) {
        return point.longitude() + "," + point.latitude() + ",name=" + name(point.name(), "장소");
    }
    private String navigationUrl(CoordinateRequest destination) {
        String encodedName = java.net.URLEncoder.encode(name(destination.name(), "목적지"),
                java.nio.charset.StandardCharsets.UTF_8);
        return "https://map.kakao.com/link/to/" + encodedName + ","
                + destination.latitude() + "," + destination.longitude();
    }
    private String name(String value, String fallback) { return value == null || value.isBlank() ? fallback : value; }
    private String text(JsonNode node, String field) {
        return node == null || node.isMissingNode() || node.isNull() ? null : node.path(field).asText(null);
    }
}
