package org.tafel.squating.api.dto;

import java.time.Instant;
import java.util.List;

public record BrandResponseDto(
    String id,
    String name,
    String primaryDomain,
    String industrySector,
    Instant registeredAt,
    Instant lastAuditedAt,
    List<String> monitoredTlds,
    int alertThreshold,
    long candidateCount,
    long criticalAlertCount
) {
}
