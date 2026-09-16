package org.tafel.squating.analysis;

import org.tafel.squating.domain.enums.ContentIndicator;

import java.util.List;

public record ContentAnalysis(
    boolean isPhishingSuspicious,
    boolean isParkedPage,
    int confidenceScore,
    List<ContentIndicator> indicators,
    List<String> matchedSignatures,
    String summary
) {
    public static ContentAnalysis clean() {
        return new ContentAnalysis(false, false, 0, List.of(), List.of(), "No malicious content signatures detected.");
    }
}
