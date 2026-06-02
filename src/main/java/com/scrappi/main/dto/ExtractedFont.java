package com.scrappi.main.dto;

public record ExtractedFont(
        String family,
        Integer weight,
        String fileUrl,
        String source
) {
}
