package com.scrappi.main.background;

import com.scrappi.main.dto.ExtractedFont;
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
public class FontExtractor {

    private static final Pattern FONT_FACE_PATTERN =
            Pattern.compile("@font-face\\s*\\{(.*?)\\}",
                    Pattern.DOTALL);

    private static final Pattern FAMILY_PATTERN =
            Pattern.compile(
                    "font-family\\s*:\\s*['\"]?([^;'\"}]*)"
            );

    private static final Pattern WEIGHT_PATTERN =
            Pattern.compile(
                    "font-weight\\s*:\\s*(\\d+)"
            );

    private static final Pattern URL_PATTERN =
            Pattern.compile(
                    "url\\(['\"]?([^'\")]+)['\"]?\\)"
            );

    public List<ExtractedFont> extract(Document document) {

        Set<String> unique = new HashSet<>();
        List<ExtractedFont> fonts = new ArrayList<>();

        Elements stylesheets =
                document.select("link[rel=stylesheet]");

        for (Element stylesheet : stylesheets) {

            try {

                String cssUrl =
                        stylesheet.absUrl("href");

                if (cssUrl.isBlank()) {
                    continue;
                }

                String css =
                        Jsoup.connect(cssUrl)
                                .ignoreContentType(true)
                                .execute()
                                .body();

                extractFromCss(
                        css,
                        cssUrl,
                        unique,
                        fonts
                );

            } catch (Exception ignored) {
            }
        }

        return fonts;
    }

    private void extractFromCss(
            String css,
            String source,
            Set<String> unique,
            List<ExtractedFont> fonts
    ) {

        Matcher fontFaceMatcher =
                FONT_FACE_PATTERN.matcher(css);

        while (fontFaceMatcher.find()) {

            String block =
                    fontFaceMatcher.group(1);

            String family =
                    extractFamily(block);

            Integer weight =
                    extractWeight(block);

            String fileUrl =
                    resolveFontUrl(source,extractUrl(block));


            if (family == null || family.toLowerCase().contains("fallback")) {
                continue;
            }

            String key =
                    family + "|" +
                            weight;

            if (!unique.add(key)) {
                continue;
            }

            fonts.add(
                    new ExtractedFont(
                            family,
                            weight,
                            fileUrl,
                            source
                    )
            );
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