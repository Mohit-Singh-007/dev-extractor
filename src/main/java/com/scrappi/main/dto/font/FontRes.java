package com.scrappi.main.dto.font;


import java.util.List;
import java.util.Set;

public record FontRes(
        String family,
        Set<Integer> weights,
        List<String> downloadUrls
) {
}