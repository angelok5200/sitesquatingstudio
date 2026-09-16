package org.tafel.squating.adapters.persistence;

import org.springframework.stereotype.Repository;
import org.tafel.squating.domain.model.RiskAssessment;
import org.tafel.squating.ports.outbound.RiskAssessmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryRiskAssessmentRepository implements RiskAssessmentRepository {

    private final Map<String, RiskAssessment> assessments = new ConcurrentHashMap<>();

    @Override
    public RiskAssessment save(RiskAssessment riskAssessment) {
        assessments.put(riskAssessment.getId(), riskAssessment);
        return riskAssessment;
    }

    @Override
    public Optional<RiskAssessment> findLatestByCandidateId(String candidateId) {
        return assessments.values().stream()
            .filter(a -> a.getCandidateId().equals(candidateId))
            .max((a1, a2) -> a1.getAssessedAt().compareTo(a2.getAssessedAt()));
    }

    @Override
    public List<RiskAssessment> findAllByCandidateId(String candidateId) {
        return assessments.values().stream()
            .filter(a -> a.getCandidateId().equals(candidateId))
            .collect(Collectors.toList());
    }
}
