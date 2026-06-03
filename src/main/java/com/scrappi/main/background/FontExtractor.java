package com.scrappi.main.background;

import com.scrappi.main.dto.font.ExtractedFont;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class FontExtractor {

    private static final Pattern FONT_FACE_PATTERN =
            Pattern.compile("@font-face\\s*\\{(.*?)\\}",
                    Pattern.DOTALL);

    private static final Pattern FAMILY_PATTERN =
            Pattern.compile("font-family\\s*:\\s*['\"]?([^;'\"\\}]+)['\"]?");

    private static final Pattern WEIGHT_PATTERN =
            Pattern.compile(
                    "font-weight\\s*:\\s*(\\d+)"
            );

    private static final Pattern URL_PATTERN =
            Pattern.compile(
                    "url\\(['\"]?([^'\")]+)['\"]?\\)"
            );

    private static final Pattern IMPORT_PATTERN =
            Pattern.compile("@import\\s+url\\(['\"]?([^'\"\\)]+)['\"]?\\)|@import\\s+['\"]([^'\"]+)['\"]");

    public List<ExtractedFont> extract(Document document) {
        Set<String> unique = new HashSet<>();
        List<ExtractedFont> fonts = new ArrayList<>();
        Set<String> visitedUrls = new HashSet<>(); // avoid infinite loops

        // handle <link rel=stylesheet>
        Elements stylesheets = document.select("link[rel=stylesheet]");
        for (Element stylesheet : stylesheets) {
            String cssUrl = stylesheet.absUrl("href");
            if (!cssUrl.isBlank()) {
                fetchAndExtract(cssUrl, unique, fonts, visitedUrls);
            }
        }

        // handle inline <style> tags
        Elements styleTags = document.select("style");
        for (Element styleTag : styleTags) {
            extractFromCss(styleTag.html(), document.baseUri(), unique, fonts, visitedUrls);
        }

        return fonts;
    }

    private void fetchAndExtract(
            String cssUrl,
            Set<String> unique,
            List<ExtractedFont> fonts,
            Set<String> visitedUrls
    ) {
        if (!visitedUrls.add(cssUrl)) return;

        try {
            String css = Jsoup.connect(cssUrl)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0") // some servers block default Jsoup UA
                    .execute()
                    .body();

            extractFromCss(css, cssUrl, unique, fonts, visitedUrls);
        } catch (Exception e) {
            log.warn("Failed to fetch CSS: {}", cssUrl);
        }
    }

    private void extractFromCss(
            String css,
            String source,
            Set<String> unique,
            List<ExtractedFont> fonts,
            Set<String> visitedUrls
    ) {
        // recursively follow @import rules
        Matcher importMatcher = IMPORT_PATTERN.matcher(css);
        while (importMatcher.find()) {
            String importUrl = importMatcher.group(1) != null
                    ? importMatcher.group(1)
                    : importMatcher.group(2);

            if (importUrl != null) {
                String resolvedUrl = resolveFontUrl(source, importUrl);
                fetchAndExtract(resolvedUrl, unique, fonts, visitedUrls);
            }
        }

        // extract @font-face blocks
        Matcher fontFaceMatcher = FONT_FACE_PATTERN.matcher(css);
        while (fontFaceMatcher.find()) {
            String block = fontFaceMatcher.group(1);
            String family = extractFamily(block);
            Integer weight = extractWeight(block);
            String fileUrl = resolveFontUrl(source, extractUrl(block));

            if (family == null || family.toLowerCase().contains("fallback")) continue;

            String key = family + "|" + weight;
            if (!unique.add(key)) continue;

            fonts.add(new ExtractedFont(family, weight, fileUrl, source));
        }
    }

    private String extractFamily(String block) {

        Matcher matcher =
                FAMILY_PATTERN.matcher(block);

        return matcher.find()
                ? matcher.group(1).trim()
                : null;
    }
    private Integer extractWeight(String block) {

        Matcher matcher =
                WEIGHT_PATTERN.matcher(block);

        return matcher.find()
                ? Integer.parseInt(
                matcher.group(1)
        )
                : 400;
    }
    private String extractUrl(String block) {

        Matcher matcher =
                URL_PATTERN.matcher(block);

        return matcher.find()
                ? matcher.group(1)
                : null;
    }
    private String resolveFontUrl(
            String cssUrl,
            String fontUrl
    ) {

        try {

            URI cssUri =
                    URI.create(cssUrl);

            return cssUri
                    .resolve(fontUrl)
                    .toString();

        } catch (Exception e) {

            return fontUrl;
        }
    }
}