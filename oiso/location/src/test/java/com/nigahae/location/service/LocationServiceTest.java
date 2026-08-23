package com.nigahae.location.service;

import com.nigahae.location.client.MapProvider;
import com.nigahae.location.client.RouteProviderResult;
import com.nigahae.location.domain.RouteTransportType;
import com.nigahae.location.dto.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LocationServiceTest {
    private final MapProvider provider = mock(MapProvider.class);
    private final LocationService service = new LocationService(provider);

    @Test
    void calculatesRouteAndAddsDisplayValues() {
        var origin = new CoordinateRequest("불국사", 129.3319, 35.7900);
        var destination = new CoordinateRequest("동궁과 월지", 129.2273, 35.8349);
        when(provider.calculateRoute(origin, destination, RouteTransportType.WALK))
                .thenReturn(new RouteProviderResult(4025, 3914, "https://map.kakao.com/route"));

        var response = service.calculateRoute(new RouteCalculationRequest(origin, destination, RouteTransportType.WALK));

        assertThat(response.distanceMeters()).isEqualTo(4025);
        assertThat(response.durationSeconds()).isEqualTo(3914);
        assertThat(response.distanceText()).isEqualTo("4.0km");
        assertThat(response.durationText()).isEqualTo("1시간 6분");
    }

    @Test
    void trimsAddressBeforeGeocoding() {
        var expected = new GeocodingResponse("경주시 불국로 385", 0, List.of());
        when(provider.geocode("경주시 불국로 385", 1, 10)).thenReturn(expected);
        assertThat(service.geocode("  경주시 불국로 385  ", 1, 10)).isSameAs(expected);
    }

    @Test
    void createsMapAndNavigationLinksWithoutCallingProvider() {
        var response = service.createMapLinks(new MapLinkRequest(new CoordinateRequest("불국사", 129.3319, 35.79)));
        assertThat(response.mapUrl()).contains("/link/map/").contains("35.79,129.3319");
        assertThat(response.navigationUrl()).contains("/link/to/");
        verifyNoInteractions(provider);
    }
}
