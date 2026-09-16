package org.tafel.squating.api.mappers;

import org.springframework.stereotype.Component;
import org.tafel.squating.api.dto.AlertDto;
import org.tafel.squating.api.dto.BrandResponseDto;
import org.tafel.squating.api.dto.CandidateDomainDto;
import org.tafel.squating.domain.model.Alert;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;

@Component
public class DomainDtoMapper {

    public BrandResponseDto toBrandResponse(Brand brand, long candidateCount, long criticalAlertCount) {
        return new BrandResponseDto(
            brand.getId(),
            brand.getName(),
            brand.getPrimaryDomain(),
            brand.getIndustrySector(),
            brand.getCreatedAt(),
            brand.getLastAuditedAt(),
            brand.getMonitoredTlds(),
            brand.getPolicy().alertRiskThreshold(),
            candidateCount,
            criticalAlertCount
        );
    }

    public CandidateDomainDto toCandidateDto(CandidateDomain candidate) {
        return new CandidateDomainDto(
            candidate.getId(),
            candidate.getBrandId(),
            candidate.getDomainName(),
            candidate.getOriginalDomain(),
            candidate.getMutationType(),
            candidate.getMutationDetail(),
            candidate.getStatus(),
            candidate.getRiskScore(),
            candidate.getRiskLevel(),
            candidate.getVisualSimilarity(),
            candidate.getPhoneticSimilarity(),
            candidate.getCologneCode(),
            candidate.getDoubleMetaphoneCode(),
            candidate.getDiscoveredAt(),
            candidate.getLastObservedAt()
        );
    }

    public AlertDto toAlertDto(Alert alert) {
        return new AlertDto(
            alert.getId(),
            alert.getBrandId(),
            alert.getCandidateId(),
            alert.getDomainName(),
            alert.getType(),
            alert.getSeverity(),
            alert.getHeadline(),
            alert.getMessage(),
            alert.isAcknowledged(),
            alert.getCreatedAt()
        );
    }
}
