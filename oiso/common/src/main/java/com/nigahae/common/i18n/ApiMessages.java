package com.nigahae.common.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Translates server-authored messages only, never arbitrary response content. */
public final class ApiMessages {
    private static final Map<String, String> ENGLISH = load();
    private static final List<Map.Entry<String, String>> FRAGMENTS = ENGLISH.entrySet().stream()
            .sorted(Comparator.comparingInt((Map.Entry<String, String> e) -> e.getKey().length()).reversed())
            .toList();

    private ApiMessages() { }

    public static String translate(String message) {
        if (!ApiLanguage.isEnglish() || message == null) return message;
        String exact = ENGLISH.get(message);
        if (exact != null) return exact;
        // Preserve field paths, IDs, allowed enum values and numeric validation limits.
        String result = message;
        for (var entry : FRAGMENTS) result = result.replace(entry.getKey(), entry.getValue());
        return result;
    }

    private static Map<String, String> load() {
        try (var stream = ApiMessages.class.getResourceAsStream("/i18n/api-en.json")) {
            if (stream == null) throw new IllegalStateException("Missing English API messages");
            return Map.copyOf(new ObjectMapper().readValue(stream, new TypeReference<Map<String, String>>() { }));
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
