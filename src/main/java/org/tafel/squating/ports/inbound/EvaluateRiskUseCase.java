package org.tafel.squating.ports.inbound;

import org.tafel.squating.domain.model.Alert;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.domain.model.RiskAssessment;

import java.util.List;

public interface EvaluateRiskUseCase {
    RiskAssessment evaluateCandidate(CandidateDomain candidate);
    List<Alert> evaluateBrandRisksAndAlert(String brandId);
    List<Alert> getPendingAlerts(String brandId);
}
