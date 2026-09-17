package com.nigahae.location.service;

import com.nigahae.location.client.MapProvider;
import com.nigahae.location.client.RouteProviderResult;
import com.nigahae.location.dto.*;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class LocationService {
    private final MapProvider mapProvider;
    public LocationService(MapProvider mapProvider) { this.mapProvider = mapProvider; }

    public RouteCalculationResponse calculateRoute(RouteCalculationRequest request) {
        RouteProviderResult route = mapProvider.calculateRoute(request.origin(), request.destination(), request.transportType());
        return new RouteCalculationResponse(CoordinateResponse.from(request.origin()), CoordinateResponse.from(request.destination()),
                request.transportType(), route.distanceMeters(), route.durationSeconds(), distanceText(route.distanceMeters()),
                durationText(route.durationSeconds()), route.navigationUrl());
    }
    public GeocodingResponse geocode(String address, int page, int size) { return mapProvider.geocode(address.trim(), page, size); }
    public ReverseGeocodingResponse reverseGeocode(double longitude, double latitude) {
        return mapProvider.reverseGeocode(longitude, latitude);
    }
    public RegionListResponse findRegions(double longitude, double latitude) {
        return new RegionListResponse(longitude, latitude, mapProvider.findRegions(longitude, latitude));
    }
    public MapLinkResponse createMapLinks(MapLinkRequest request) {
        CoordinateRequest point = request.destination();
        String name = URLEncoder.encode(point.name() == null || point.name().isBlank() ? "목적지" : point.name(), StandardCharsets.UTF_8);
        String coordinate = point.latitude() + "," + point.longitude();
        return new MapLinkResponse(CoordinateResponse.from(point),
                "https://map.kakao.com/link/map/" + name + "," + coordinate,
                "https://map.kakao.com/link/to/" + name + "," + coordinate);
    }
    private String distanceText(int meters) {
        return meters < 1000 ? meters + "m" : String.format(Locale.ROOT, "%.1fkm", meters / 1000.0);
    }
    private String durationText(int seconds) {
        int minutes = (int) Math.ceil(seconds / 60.0);
        if (com.nigahae.common.i18n.ApiLanguage.isEnglish()) {
            return minutes < 60 ? minutes + " min" : (minutes / 60) + " hr"
                    + (minutes % 60 == 0 ? "" : " " + minutes % 60 + " min");
        }
        return minutes < 60 ? minutes + "분" : (minutes / 60) + "시간" + (minutes % 60 == 0 ? "" : " " + minutes % 60 + "분");
    }
}
