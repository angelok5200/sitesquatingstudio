package org.tafel.squating.domain.value;

import org.tafel.squating.domain.enums.ContentIndicator;

import java.util.List;

public record HttpSnapshot(
    boolean isLive,
    int statusCode,
    String pageTitle,
    String finalUrl,
    long contentLength,
    List<ContentIndicator> indicators
) {
    public static HttpSnapshot unreachable() {
        return new HttpSnapshot(false, 0, null, null, 0, List.of());
    }
}
