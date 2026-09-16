package org.tafel.squating.adapters.persistence;

import org.springframework.stereotype.Repository;
import org.tafel.squating.domain.model.Evidence;
import org.tafel.squating.ports.outbound.EvidenceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryEvidenceRepository implements EvidenceRepository {

    private final Map<String, Evidence> evidences = new ConcurrentHashMap<>();

    @Override
    public Evidence save(Evidence evidence) {
        evidences.put(evidence.getId(), evidence);
        return evidence;
    }

    @Override
    public List<Evidence> saveAll(List<Evidence> list) {
        for (Evidence e : list) {
            evidences.put(e.getId(), e);
        }
        return list;
    }

    @Override
    public List<Evidence> findByCandidateId(String candidateId) {
        return evidences.values().stream()
            .filter(e -> e.getCandidateId().equals(candidateId))
            .collect(Collectors.toList());
    }
}
