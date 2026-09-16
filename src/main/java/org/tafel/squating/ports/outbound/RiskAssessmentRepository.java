package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.model.RiskAssessment;

import java.util.List;
import java.util.Optional;

public interface RiskAssessmentRepository {
    RiskAssessment save(RiskAssessment riskAssessment);
    Optional<RiskAssessment> findLatestByCandidateId(String candidateId);
    List<RiskAssessment> findAllByCandidateId(String candidateId);
}
