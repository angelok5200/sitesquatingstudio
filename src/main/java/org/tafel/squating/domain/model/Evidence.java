package org.tafel.squating.domain.model;

import org.tafel.squating.domain.enums.EvidenceOwnerType;
import org.tafel.squating.domain.enums.EvidenceType;

import java.time.Instant;
import java.util.UUID;

/**
 * Concrete forensic evidence item collected during domain inspection.
 */
public class Evidence {
    private final String id;
    private final String candidateId;
    private final EvidenceType type;
    private final EvidenceOwnerType ownerType;
    private final String title;
    private final String detail;
    private final int severityWeight;
    private final Instant collectedAt;

    public Evidence(String id, String candidateId, EvidenceType type, EvidenceOwnerType ownerType, String title, String detail, int severityWeight) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.candidateId = candidateId;
        this.type = type;
        this.ownerType = ownerType;
        this.title = title;
        this.detail = detail;
        this.severityWeight = severityWeight;
        this.collectedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getCandidateId() { return candidateId; }
    public EvidenceType getType() { return type; }
    public EvidenceOwnerType getOwnerType() { return ownerType; }
    public String getTitle() { return title; }
    public String getDetail() { return detail; }
    public int getSeverityWeight() { return severityWeight; }
    public Instant getCollectedAt() { return collectedAt; }
}
