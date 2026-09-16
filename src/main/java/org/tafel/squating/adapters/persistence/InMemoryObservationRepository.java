package org.tafel.squating.adapters.persistence;

import org.springframework.stereotype.Repository;
import org.tafel.squating.domain.model.DomainObservation;
import org.tafel.squating.ports.outbound.ObservationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryObservationRepository implements ObservationRepository {

    private final Map<String, DomainObservation> observations = new ConcurrentHashMap<>();

    @Override
    public DomainObservation save(DomainObservation observation) {
        observations.put(observation.getId(), observation);
        return observation;
    }

    @Override
    public Optional<DomainObservation> findLatestByCandidateId(String candidateId) {
        return observations.values().stream()
            .filter(o -> o.getCandidateId().equals(candidateId))
            .max((o1, o2) -> o1.getObservedAt().compareTo(o2.getObservedAt()));
    }

    @Override
    public List<DomainObservation> findAllByCandidateId(String candidateId) {
        return observations.values().stream()
            .filter(o -> o.getCandidateId().equals(candidateId))
            .collect(Collectors.toList());
    }
}
