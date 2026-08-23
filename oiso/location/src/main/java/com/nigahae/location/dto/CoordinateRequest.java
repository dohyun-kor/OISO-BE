package com.nigahae.location.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
public record CoordinateRequest(
 @Schema(example="불국사") @Size(max=100) String name,
 @Schema(description="경도(X). -180 이상 180 이하", example="129.3319")
 @NotNull(message="경도(longitude, X)는 필수입니다.")
 @DecimalMin(value="-180", message="경도(longitude, X)는 -180 이상이어야 합니다.")
 @DecimalMax(value="180", message="경도(longitude, X)는 180 이하여야 합니다.") Double longitude,
 @Schema(description="위도(Y). -90 이상 90 이하", example="35.7900")
 @NotNull(message="위도(latitude, Y)는 필수입니다.")
 @DecimalMin(value="-90", message="위도(latitude, Y)는 -90 이상이어야 합니다. 경도와 순서를 확인해 주세요.")
 @DecimalMax(value="90", message="위도(latitude, Y)는 90 이하여야 합니다. 경도와 순서를 확인해 주세요.") Double latitude) {}
