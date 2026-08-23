package com.nigahae.location.dto;
public record CoordinateResponse(String name, double longitude, double latitude) {
 public static CoordinateResponse from(CoordinateRequest value) {
  return new CoordinateResponse(value.name(), value.longitude(), value.latitude());
 }
}
