package org.tafel.squating.api.dto;

import org.tafel.squating.domain.enums.AlertType;
import org.tafel.squating.domain.enums.RiskLevel;

import java.time.Instant;

public record AlertDto(
    String id,
    String brandId,
    String candidateId,
    String domainName,
    AlertType type,
    RiskLevel severity,
    String headline,
    String message,
    boolean acknowledged,
    Instant createdAt
) {
}
