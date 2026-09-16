package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.model.Evidence;

import java.util.List;

public interface EvidenceRepository {
    Evidence save(Evidence evidence);
    List<Evidence> saveAll(List<Evidence> evidences);
    List<Evidence> findByCandidateId(String candidateId);
}
