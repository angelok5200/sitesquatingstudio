package org.tafel.squating.api.dto;

import org.tafel.squating.domain.enums.CandidateStatus;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.enums.RiskLevel;

import java.time.Instant;

public record CandidateDomainDto(
    String id,
    String brandId,
    String domainName,
    String originalDomain,
    MutationType mutationType,
    String mutationRationale,
    CandidateStatus status,
    int riskScore,
    RiskLevel riskLevel,
    double visualSimilarity,
    double phoneticSimilarity,
    String cologneCode,
    String doubleMetaphoneCode,
    Instant discoveredAt,
    Instant lastObservedAt
) {
}
