package com.scrappi.main.dto.scan;

import jakarta.validation.constraints.NotBlank;

public record ScanReq(
        @NotBlank String url
) {
}
