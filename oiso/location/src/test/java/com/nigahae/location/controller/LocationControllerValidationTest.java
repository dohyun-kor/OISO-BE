package com.nigahae.location.controller;

import com.nigahae.location.domain.RouteTransportType;
import com.nigahae.location.dto.*;
import com.nigahae.location.service.LocationService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LocationControllerValidationTest {

    @Test
    void inheritedParameterConstraintsDoNotConflictWithControllerMethods() throws Exception {
        var controller = new LocationController(mock(LocationService.class));
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var executable = factory.getValidator().forExecutables();
            var point = new CoordinateRequest("불국사", 129.3319, 35.7900);

            assertThat(executable.validateParameters(controller,
                    method("calculateRoute", RouteCalculationRequest.class),
                    new Object[]{new RouteCalculationRequest(point, point, RouteTransportType.WALK)})).isEmpty();
            assertThat(executable.validateParameters(controller,
                    method("calculateRoute", RouteCalculationRequest.class),
                    new Object[]{new RouteCalculationRequest(point, point, RouteTransportType.CAR)})).isEmpty();
            assertThat(executable.validateParameters(controller,
                    method("mapLinks", MapLinkRequest.class),
                    new Object[]{new MapLinkRequest(point)})).isEmpty();
            assertThat(executable.validateParameters(controller,
                    method("geocode", String.class, int.class, int.class),
                    new Object[]{"경주시 불국로 385", 1, 10})).isEmpty();
            assertThat(executable.validateParameters(controller,
                    method("reverseGeocode", double.class, double.class),
                    new Object[]{129.3319, 35.7900})).isEmpty();
            assertThat(executable.validateParameters(controller,
                    method("regions", double.class, double.class),
                    new Object[]{129.3319, 35.7900})).isEmpty();
        }
    }

    @Test
    void rejectsLatitudeOverNinetyThroughInheritedConstraint() throws Exception {
        var controller = new LocationController(mock(LocationService.class));
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var violations = factory.getValidator().forExecutables().validateParameters(controller,
                    method("reverseGeocode", double.class, double.class),
                    new Object[]{35.7900, 129.3319});
            assertThat(violations).singleElement()
                    .satisfies(violation -> assertThat(violation.getMessage()).contains("위도(latitude, Y)"));
        }
    }

    private Method method(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return LocationController.class.getMethod(name, parameterTypes);
    }
}
