package org.tafel.squating.application;

import org.springframework.stereotype.Service;
import org.tafel.squating.domain.enums.EvidenceOwnerType;
import org.tafel.squating.domain.enums.EvidenceType;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.domain.model.DomainObservation;
import org.tafel.squating.domain.model.Evidence;
import org.tafel.squating.domain.value.*;
import org.tafel.squating.ports.outbound.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DomainInspectionService {

    private final DnsInspector dnsInspector;
    private final WebInspector webInspector;
    private final TlsInspector tlsInspector;
    private final MailInspector mailInspector;
    private final DomainRegistrationLookup registrationLookup;
    private final ObservationRepository observationRepository;
    private final EvidenceRepository evidenceRepository;

    public DomainInspectionService(
        DnsInspector dnsInspector,
        WebInspector webInspector,
        TlsInspector tlsInspector,
        MailInspector mailInspector,
        DomainRegistrationLookup registrationLookup,
        ObservationRepository observationRepository,
        EvidenceRepository evidenceRepository
    ) {
        this.dnsInspector = dnsInspector;
        this.webInspector = webInspector;
        this.tlsInspector = tlsInspector;
        this.mailInspector = mailInspector;
        this.registrationLookup = registrationLookup;
        this.observationRepository = observationRepository;
        this.evidenceRepository = evidenceRepository;
    }

    public DomainObservation inspectDomain(CandidateDomain candidate) {
        String domain = candidate.getDomainName();

        RegistrationSnapshot reg = registrationLookup.lookupRegistration(domain);
        DnsSnapshot dns = dnsInspector.inspectDns(domain);
        HttpSnapshot http = dns.hasDns() ? webInspector.inspectWeb(domain) : HttpSnapshot.unreachable();
        TlsSnapshot tls = dns.hasDns() ? tlsInspector.inspectTls(domain) : TlsSnapshot.none();
        MailSnapshot mail = mailInspector.inspectMail(domain);

        DomainObservation observation = new DomainObservation(
            null,
            candidate.getId(),
            reg,
            dns,
            http,
            tls,
            mail
        );
        observationRepository.save(observation);

        // Collect technical evidences
        List<Evidence> evidences = new ArrayList<>();
        if (dns.hasDns()) {
            evidences.add(new Evidence(
                null,
                candidate.getId(),
                EvidenceType.DNS_A_RECORD,
                EvidenceOwnerType.CANDIDATE,
                "Active IPv4 Resolution",
                "Resolves to: " + String.join(", ", dns.aRecords()),
                20
            ));
        }

        if (mail.hasMx()) {
            evidences.add(new Evidence(
                null,
                candidate.getId(),
                EvidenceType.DNS_MX_EXCHANGE,
                EvidenceOwnerType.THREAT_ACTOR,
                "Active Mail Exchangers",
                "Routing mail to: " + String.join(", ", mail.mxHosts()),
                40
            ));
        }

        if (tls.hasCertificate()) {
            evidences.add(new Evidence(
                null,
                candidate.getId(),
                EvidenceType.SSL_CERTIFICATE_ISSUER,
                EvidenceOwnerType.CANDIDATE,
                "TLS Certificate Issued",
                "Issuer: " + tls.issuerCommonName() + " | Subject: " + tls.subjectCommonName(),
                15
            ));
        }

        if (http.isLive() && !http.indicators().isEmpty()) {
            evidences.add(new Evidence(
                null,
                candidate.getId(),
                EvidenceType.HTTP_LOGIN_FORM,
                EvidenceOwnerType.THREAT_ACTOR,
                "Web Form Indicators",
                "Matched indicators: " + http.indicators(),
                35
            ));
        }

        evidenceRepository.saveAll(evidences);
        candidate.setLastObservedAt(Instant.now());
        return observation;
    }
}
