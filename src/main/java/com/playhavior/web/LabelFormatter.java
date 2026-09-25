package com.playhavior.web;

import org.springframework.stereotype.Component;

/*
 * Turns enum constants into readable labels for templates,
 * e.g. HARASSMENT_BULLYING -> "Harassment Bullying".
 * Usage in Thymeleaf: ${@labels.of(pathway.pathwayMode)}
 */
@Component("labels")
public class LabelFormatter {

    public String of(Enum<?> value) {
        if (value == null) {
            return "";
        }

        StringBuilder label = new StringBuilder();

        for (String word : value.name().split("_")) {
            if (!label.isEmpty()) {
                label.append(' ');
            }
            label.append(word.charAt(0))
                    .append(word.substring(1).toLowerCase());
        }

        return label.toString();
    }
}
