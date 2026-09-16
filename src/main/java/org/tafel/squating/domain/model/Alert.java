package org.tafel.squating.domain.model;

import org.tafel.squating.domain.enums.AlertType;
import org.tafel.squating.domain.enums.RiskLevel;

import java.time.Instant;
import java.util.UUID;

/**
 * High-priority security incident notification generated for brand stakeholders.
 */
public class Alert {
    private final String id;
    private final String brandId;
    private final String candidateId;
    private final String domainName;
    private final AlertType type;
    private final RiskLevel severity;
    private final String headline;
    private final String message;
    private boolean acknowledged;
    private Instant createdAt;

    public Alert(
        String id,
        String brandId,
        String candidateId,
        String domainName,
        AlertType type,
        RiskLevel severity,
        String headline,
        String message
    ) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.brandId = brandId;
        this.candidateId = candidateId;
        this.domainName = domainName;
        this.type = type;
        this.severity = severity;
        this.headline = headline;
        this.message = message;
        this.acknowledged = false;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getBrandId() { return brandId; }
    public String getCandidateId() { return candidateId; }
    public String getDomainName() { return domainName; }
    public AlertType getType() { return type; }
    public RiskLevel getSeverity() { return severity; }
    public String getHeadline() { return headline; }
    public String getMessage() { return message; }
    public boolean isAcknowledged() { return acknowledged; }
    public Instant getCreatedAt() { return createdAt; }

    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
}
