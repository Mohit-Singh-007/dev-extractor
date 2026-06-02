package com.scrappi.main.utils;


import com.scrappi.main.dto.TechRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Component
public class TechnologyDetector {

    private static final List<TechRule> RULES = List.of(

            new TechRule(
                    "React",
                    List.of(
                            "__react",
                            "react-dom",
                            "react"
                    )
            ),

            new TechRule(
                    "Next.js",
                    List.of(
                            "__next_data__",
                            "_next/static",
                            "__next"
                    )
            ),

            new TechRule(
                    "Angular",
                    List.of(
                            "ng-version",
                            "angular"
                    )
            ),

            new TechRule(
                    "Vue.js",
                    List.of(
                            "vue.js",
                            "vue-router",
                            "data-v-"
                    )
            ),

            new TechRule(
                    "Nuxt.js",
                    List.of(
                            "__nuxt"
                    )
            ),

            new TechRule(
                    "WordPress",
                    List.of(
                            "wp-content",
                            "wp-includes"
                    )
            ),

            new TechRule(
                    "Tailwind CSS",
                    List.of(
                            "tailwind",
                            "tailwindcss"
                    )
            ),

            new TechRule(
                    "Bootstrap",
                    List.of(
                            "bootstrap.min.css",
                            "bootstrap.min.js",
                            "bootstrap"
                    )
            ),

            new TechRule(
                    "jQuery",
                    List.of(
                            "jquery"
                    )
            ),

            new TechRule(
                    "Firebase",
                    List.of(
                            "firebase",
                            "firebaseapp.com"
                    )
            ),

            new TechRule(
                    "Supabase",
                    List.of(
                            "supabase"
                    )
            ),

            new TechRule(
                    "Stripe",
                    List.of(
                            "stripe"
                    )
            )
    );

    public List<String> detect(String html) {

        html = html.toLowerCase();

        Set<String> detected = new HashSet<>();

        for (TechRule rule : RULES) {

            boolean matched =
                    rule.signatures()
                            .stream()
                            .anyMatch(html::contains);

            if (matched) {
                detected.add(rule.techName());
            }
        }

        return new ArrayList<>(detected);
    }
}