package com.nigahae.common.i18n;

import java.util.Locale;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Request-only language selection; background jobs and requests without a header use Korean. */
public final class ApiLanguage {
    private ApiLanguage() { }

    public static boolean isEnglish() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) return false;
        return "en".equals(resolve(attributes.getRequest().getHeader("Accept-Language")));
    }

    public static String resolve(String header) {
        if (header == null || header.isBlank()) return "ko";
        try {
            var ranges = Locale.LanguageRange.parse(header);
            for (var range : ranges) {
                if (range.getWeight() <= 0) continue;
                String base = range.getRange().split("-")[0];
                if (base.equals("en") || base.equals("ko")) return base;
            }
        } catch (IllegalArgumentException ignored) { }
        return "ko";
    }

    public static String choose(String korean, String english) {
        return isEnglish() && english != null && !english.isBlank() ? english : korean;
    }
}
