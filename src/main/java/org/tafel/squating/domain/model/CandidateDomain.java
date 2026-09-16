package org.tafel.squating.domain.model;

import org.tafel.squating.domain.enums.CandidateStatus;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.enums.RiskLevel;

import java.time.Instant;
import java.util.UUID;

/**
 * Candidate Domain generated through mutation/typosquatting of the brand.
 */
public class CandidateDomain {
    private final String id;
    private final String brandId;
    private final String domainName;
    private final String originalDomain;
    private final MutationType mutationType;
    private final String mutationDetail;
    private CandidateStatus status;
    private RiskLevel riskLevel;
    private int riskScore;
    private double visualSimilarity;
    private double phoneticSimilarity;
    private String cologneCode;
    private String doubleMetaphoneCode;
    private Instant discoveredAt;
    private Instant lastObservedAt;

    public CandidateDomain(
        String id,
        String brandId,
        String domainName,
        String originalDomain,
        MutationType mutationType,
        String mutationDetail
    ) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.brandId = brandId;
        this.domainName = domainName.toLowerCase().trim();
        this.originalDomain = originalDomain.toLowerCase().trim();
        this.mutationType = mutationType;
        this.mutationDetail = mutationDetail;
        this.status = CandidateStatus.GENERATED;
        this.riskLevel = RiskLevel.SAFE;
        this.riskScore = 0;
        this.discoveredAt = Instant.now();
    }

    public String getId() { return id; }
    public String getBrandId() { return brandId; }
    public String getDomainName() { return domainName; }
    public String getOriginalDomain() { return originalDomain; }
    public MutationType getMutationType() { return mutationType; }
    public String getMutationDetail() { return mutationDetail; }
    public CandidateStatus getStatus() { return status; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public int getRiskScore() { return riskScore; }
    public double getVisualSimilarity() { return visualSimilarity; }
    public double getPhoneticSimilarity() { return phoneticSimilarity; }
    public String getCologneCode() { return cologneCode; }
    public String getDoubleMetaphoneCode() { return doubleMetaphoneCode; }
    public Instant getDiscoveredAt() { return discoveredAt; }
    public Instant getLastObservedAt() { return lastObservedAt; }

    public void setStatus(CandidateStatus status) { this.status = status; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public void setVisualSimilarity(double visualSimilarity) { this.visualSimilarity = visualSimilarity; }
    public void setPhoneticSimilarity(double phoneticSimilarity) { this.phoneticSimilarity = phoneticSimilarity; }
    public void setCologneCode(String cologneCode) { this.cologneCode = cologneCode; }
    public void setDoubleMetaphoneCode(String doubleMetaphoneCode) { this.doubleMetaphoneCode = doubleMetaphoneCode; }
    public void setLastObservedAt(Instant lastObservedAt) { this.lastObservedAt = lastObservedAt; }
}
