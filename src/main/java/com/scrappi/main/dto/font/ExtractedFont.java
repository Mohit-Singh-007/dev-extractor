package com.scrappi.main.dto.font;

public record ExtractedFont(
        String family,
        Integer weight,
        String fileUrl,
        String source
) {
}
