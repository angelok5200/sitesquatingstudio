package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.enums.CandidateStatus;
import org.tafel.squating.domain.enums.RiskLevel;
import org.tafel.squating.domain.model.CandidateDomain;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository {
    CandidateDomain save(CandidateDomain candidate);
    List<CandidateDomain> saveAll(List<CandidateDomain> candidates);
    Optional<CandidateDomain> findById(String id);
    Optional<CandidateDomain> findByDomainName(String domainName);
    List<CandidateDomain> findByBrandId(String brandId);
    List<CandidateDomain> findByBrandIdAndRiskLevel(String brandId, RiskLevel riskLevel);
    List<CandidateDomain> findByBrandIdAndStatus(String brandId, CandidateStatus status);
    List<CandidateDomain> findAll();
    long count();
}
