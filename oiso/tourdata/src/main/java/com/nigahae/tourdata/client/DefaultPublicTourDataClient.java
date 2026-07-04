package com.nigahae.tourdata.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nigahae.tourdata.config.TourDataProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class DefaultPublicTourDataClient implements PublicTourDataClient {

    private static final String SUCCESS_CODE = "0000";

    private final RestClient restClient;
    private final TourDataProperties properties;
    private final ObjectMapper objectMapper;

    public DefaultPublicTourDataClient(
            RestClient tourApiRestClient,
            TourDataProperties properties,
            ObjectMapper objectMapper
    ) {
        this.restClient = tourApiRestClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<TourContentPayload> fetchAllTourContents() {
        validateServiceKey();

        List<TourContentPayload> results = new ArrayList<>();
        List<String> contentTypeIds = properties.getApi().getContentTypeIds();
        if (contentTypeIds == null || contentTypeIds.isEmpty()) {
            contentTypeIds = List.of("");
        }

        for (String contentTypeId : contentTypeIds) {
            results.addAll(fetchByContentType(contentTypeId));
        }
        return results;
    }

    @Override
    public List<AccommodationRoomPayload> fetchAccommodationRooms(Long contentId, Long contentTypeId) {
        validateServiceKey();

        List<AccommodationRoomPayload> results = new ArrayList<>();
        int pageNo = 1;
        int totalCount = Integer.MAX_VALUE;

        while (results.size() < totalCount) {
            JsonNode body = requestPage(buildDetailInfoUri(contentId, contentTypeId, pageNo));
            totalCount = body.path("totalCount").asInt(results.size());

            JsonNode items = body.path("items").path("item");
            int itemCount = appendAccommodationRooms(results, items);
            if (itemCount == 0 || pageNo * properties.getApi().getPageSize() >= totalCount) {
                break;
            }
            pageNo++;
        }

        return results;
    }

    private List<TourContentPayload> fetchByContentType(String contentTypeId) {
        List<TourContentPayload> results = new ArrayList<>();
        int pageNo = 1;
        int totalCount = Integer.MAX_VALUE;

        while (results.size() < totalCount) {
            JsonNode body = requestPage(buildAreaBasedListUri(contentTypeId, pageNo));
            totalCount = body.path("totalCount").asInt(results.size());

            JsonNode items = body.path("items").path("item");
            int itemCount = appendItems(results, items);
            if (itemCount == 0 || pageNo * properties.getApi().getPageSize() >= totalCount) {
                break;
            }
            pageNo++;
        }

        return results;
    }

    private void validateServiceKey() {
        if (properties.getApi().getServiceKey() == null || properties.getApi().getServiceKey().isBlank()) {
            throw new TourApiException("tour-data.api.service-key is required");
        }
    }

    private JsonNode requestPage(Function<UriBuilder, URI> uriFunction) {
        JsonNode root = restClient.get()
                .uri(uriFunction)
                .retrieve()
                .body(JsonNode.class);

        if (root == null) {
            throw new TourApiException("Tour API returned an empty response");
        }

        JsonNode response = root.path("response");
        JsonNode header = response.path("header");
        String resultCode = header.path("resultCode").asText(SUCCESS_CODE);
        if (!SUCCESS_CODE.equals(resultCode)) {
            String message = header.path("resultMsg").asText("unknown error");
            throw new TourApiException("Tour API error: " + resultCode + " " + message);
        }

        return response.path("body");
    }

    private Function<UriBuilder, URI> buildAreaBasedListUri(String contentTypeId, int pageNo) {
        return uriBuilder -> uriBuilder
                .path(properties.getApi().getOperationPath())
                .queryParam("serviceKey", properties.getApi().getServiceKey())
                .queryParam("MobileOS", properties.getApi().getMobileOs())
                .queryParam("MobileApp", properties.getApi().getMobileApp())
                .queryParam("_type", "json")
                .queryParam("arrange", properties.getApi().getArrange())
                .queryParam("numOfRows", properties.getApi().getPageSize())
                .queryParam("pageNo", pageNo)
                .queryParam("contentTypeId", contentTypeId)
                .queryParam("lDongRegnCd", properties.getApi().getLDongRegnCd())
                .queryParam("lDongSignguCd", properties.getApi().getLDongSignguCd())
                .build();
    }

    private Function<UriBuilder, URI> buildDetailInfoUri(Long contentId, Long contentTypeId, int pageNo) {
        return uriBuilder -> uriBuilder
                .path(properties.getApi().getDetailInfoOperationPath())
                .queryParam("serviceKey", properties.getApi().getServiceKey())
                .queryParam("MobileOS", properties.getApi().getMobileOs())
                .queryParam("MobileApp", properties.getApi().getMobileApp())
                .queryParam("_type", "json")
                .queryParam("contentId", contentId)
                .queryParam("contentTypeId", contentTypeId)
                .queryParam("numOfRows", properties.getApi().getPageSize())
                .queryParam("pageNo", pageNo)
                .build();
    }

    private int appendItems(List<TourContentPayload> results, JsonNode items) {
        if (items == null || items.isMissingNode() || items.isNull()) {
            return 0;
        }

        int count = 0;
        if (items.isArray()) {
            for (JsonNode item : items) {
                results.add(TourContentPayload.from(item, objectMapper));
                count++;
            }
            return count;
        }

        results.add(TourContentPayload.from(items, objectMapper));
        return 1;
    }

    private int appendAccommodationRooms(List<AccommodationRoomPayload> results, JsonNode items) {
        if (items == null || items.isMissingNode() || items.isNull()) {
            return 0;
        }

        int count = 0;
        if (items.isArray()) {
            for (JsonNode item : items) {
                results.add(AccommodationRoomPayload.from(item));
                count++;
            }
            return count;
        }

        results.add(AccommodationRoomPayload.from(items));
        return 1;
    }
}
