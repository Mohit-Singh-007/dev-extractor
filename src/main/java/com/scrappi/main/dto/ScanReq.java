package com.scrappi.main.dto;

import jakarta.validation.constraints.NotBlank;

public record ScanReq(
        @NotBlank String url
) {
}
