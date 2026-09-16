package org.tafel.squating.application;

import org.springframework.stereotype.Service;
import org.tafel.squating.domain.enums.CandidateStatus;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.domain.value.RegistrationSnapshot;
import org.tafel.squating.ports.outbound.CandidateRepository;
import org.tafel.squating.ports.outbound.CertificateDiscovery;
import org.tafel.squating.ports.outbound.DomainRegistrationLookup;

import java.util.List;

@Service
public class RegistrationDiscoveryService {

    private final DomainRegistrationLookup registrationLookup;
    private final CertificateDiscovery certificateDiscovery;
    private final CandidateRepository candidateRepository;

    public RegistrationDiscoveryService(
        DomainRegistrationLookup registrationLookup,
        CertificateDiscovery certificateDiscovery,
        CandidateRepository candidateRepository
    ) {
        this.registrationLookup = registrationLookup;
        this.certificateDiscovery = certificateDiscovery;
        this.candidateRepository = candidateRepository;
    }

    public RegistrationSnapshot discoverRegistration(CandidateDomain candidate) {
        RegistrationSnapshot snapshot = registrationLookup.lookupRegistration(candidate.getDomainName());
        if (snapshot.isRegistered()) {
            if (candidate.getStatus() == CandidateStatus.GENERATED) {
                candidate.setStatus(CandidateStatus.REGISTERED_INACTIVE);
            }
        } else {
            candidate.setStatus(CandidateStatus.UNREGISTERED);
        }
        candidateRepository.save(candidate);
        return snapshot;
    }

    public List<String> discoverCertificatesForBrand(String brandName) {
        return certificateDiscovery.discoverNewCertificates(brandName);
    }
}
