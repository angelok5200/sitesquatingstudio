package org.tafel.squating.adapters.persistence;

import org.springframework.stereotype.Repository;
import org.tafel.squating.domain.enums.CandidateStatus;
import org.tafel.squating.domain.enums.RiskLevel;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.ports.outbound.CandidateRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryCandidateRepository implements CandidateRepository {

    private final Map<String, CandidateDomain> candidates = new ConcurrentHashMap<>();

    @Override
    public CandidateDomain save(CandidateDomain candidate) {
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public List<CandidateDomain> saveAll(List<CandidateDomain> list) {
        for (CandidateDomain c : list) {
            candidates.put(c.getId(), c);
        }
        return list;
    }

    @Override
    public Optional<CandidateDomain> findById(String id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Optional<CandidateDomain> findByDomainName(String domainName) {
        return candidates.values().stream()
            .filter(c -> c.getDomainName().equalsIgnoreCase(domainName))
            .findFirst();
    }

    @Override
    public List<CandidateDomain> findByBrandId(String brandId) {
        return candidates.values().stream()
            .filter(c -> c.getBrandId() != null && c.getBrandId().equals(brandId))
            .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDomain> findByBrandIdAndRiskLevel(String brandId, RiskLevel riskLevel) {
        return candidates.values().stream()
            .filter(c -> c.getBrandId() != null && c.getBrandId().equals(brandId) && c.getRiskLevel() == riskLevel)
            .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDomain> findByBrandIdAndStatus(String brandId, CandidateStatus status) {
        return candidates.values().stream()
            .filter(c -> c.getBrandId() != null && c.getBrandId().equals(brandId) && c.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDomain> findAll() {
        return new ArrayList<>(candidates.values());
    }

    @Override
    public long count() {
        return candidates.size();
    }
}
