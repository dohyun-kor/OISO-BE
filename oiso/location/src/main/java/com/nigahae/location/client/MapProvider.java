package com.nigahae.location.client;
import com.nigahae.location.domain.RouteTransportType;
import com.nigahae.location.dto.*;
import java.util.List;
public interface MapProvider {
 RouteProviderResult calculateRoute(CoordinateRequest origin, CoordinateRequest destination, RouteTransportType type);
 GeocodingResponse geocode(String address, int page, int size);
 ReverseGeocodingResponse reverseGeocode(double longitude, double latitude);
 List<RegionResponse> findRegions(double longitude, double latitude);
}
