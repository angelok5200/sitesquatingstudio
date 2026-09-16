package org.tafel.squating.domain.model;

import org.tafel.squating.domain.enums.RiskLevel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Audit result quantifying squatting and phishing risk for a candidate domain.
 */
public class RiskAssessment {
    private final String id;
    private final String candidateId;
    private final int totalScore;
    private final RiskLevel riskLevel;
    private final List<String> triggeredRuleDescriptions;
    private final String primaryVector;
    private final boolean requiresImmediateTakedown;
    private final Instant assessedAt;

    public RiskAssessment(
        String id,
        String candidateId,
        int totalScore,
        RiskLevel riskLevel,
        List<String> triggeredRuleDescriptions,
        String primaryVector,
        boolean requiresImmediateTakedown
    ) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.candidateId = candidateId;
        this.totalScore = Math.min(100, Math.max(0, totalScore));
        this.riskLevel = riskLevel;
        this.triggeredRuleDescriptions = triggeredRuleDescriptions != null ? triggeredRuleDescriptions : List.of();
        this.primaryVector = primaryVector;
        this.requiresImmediateTakedown = requiresImmediateTakedown;
        this.assessedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getCandidateId() { return candidateId; }
    public int getTotalScore() { return totalScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public List<String> getTriggeredRuleDescriptions() { return triggeredRuleDescriptions; }
    public String getPrimaryVector() { return primaryVector; }
    public boolean isRequiresImmediateTakedown() { return requiresImmediateTakedown; }
    public Instant getAssessedAt() { return assessedAt; }
}
