package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.model.DomainObservation;

import java.util.List;
import java.util.Optional;

public interface ObservationRepository {
    DomainObservation save(DomainObservation observation);
    Optional<DomainObservation> findLatestByCandidateId(String candidateId);
    List<DomainObservation> findAllByCandidateId(String candidateId);
}
