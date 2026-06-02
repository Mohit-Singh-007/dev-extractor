package com.scrappi.main.dto;

import com.scrappi.main.model.ScanStatus;

import java.util.List;


public record ScanRes(
        Long id,
        String url,
        ScanStatus status,
        String title,
        String description,
        Integer totalLinks,
        Integer totalImages,
        Integer totalScripts,
        List<TechRes> technologies,
        List<FontRes> fonts
) {}
