package com.nigahae.tourspot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TourSpotMemoRequest(
        @NotBlank(message = "메모 내용은 필수입니다.")
        @Size(max = 1000, message = "메모는 1,000자 이하여야 합니다.")
        String content,
        @Size(max = 2048, message = "이미지 URL은 2,048자 이하여야 합니다.")
        String imageUrl
) {
    public TourSpotMemoRequest(String content) {
        this(content, null);
    }
}
