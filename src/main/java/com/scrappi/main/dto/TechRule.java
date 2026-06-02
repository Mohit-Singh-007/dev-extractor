package com.scrappi.main.dto;

import java.util.List;

public record TechRule(
        String techName,
        List<String> signatures
) {
}
