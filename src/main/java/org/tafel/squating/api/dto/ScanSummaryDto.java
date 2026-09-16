package org.tafel.squating.api.dto;

import java.time.Instant;
import java.util.Map;

public record ScanSummaryDto(
    String brandId,
    String brandName,
    int totalCandidates,
    int registeredCount,
    int activeWebCount,
    int activeMailCount,
    int criticalCount,
    int highCount,
    int mediumCount,
    int safeCount,
    Instant scannedAt,
    Map<String, Integer> mutationsBreakdown
) {
}
