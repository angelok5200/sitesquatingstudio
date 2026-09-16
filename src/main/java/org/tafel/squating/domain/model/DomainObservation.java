package org.tafel.squating.domain.model;

import org.tafel.squating.domain.value.*;

import java.time.Instant;
import java.util.UUID;

/**
 * An immutable technical observation snapshot of a candidate domain.
 */
public class DomainObservation {
    private final String id;
    private final String candidateId;
    private final RegistrationSnapshot registration;
    private final DnsSnapshot dns;
    private final HttpSnapshot http;
    private final TlsSnapshot tls;
    private final MailSnapshot mail;
    private final Instant observedAt;

    public DomainObservation(
        String id,
        String candidateId,
        RegistrationSnapshot registration,
        DnsSnapshot dns,
        HttpSnapshot http,
        TlsSnapshot tls,
        MailSnapshot mail
    ) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.candidateId = candidateId;
        this.registration = registration != null ? registration : RegistrationSnapshot.unregistered();
        this.dns = dns != null ? dns : DnsSnapshot.empty();
        this.http = http != null ? http : HttpSnapshot.unreachable();
        this.tls = tls != null ? tls : TlsSnapshot.none();
        this.mail = mail != null ? mail : MailSnapshot.safe();
        this.observedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getCandidateId() { return candidateId; }
    public RegistrationSnapshot getRegistration() { return registration; }
    public DnsSnapshot getDns() { return dns; }
    public HttpSnapshot getHttp() { return http; }
    public TlsSnapshot getTls() { return tls; }
    public MailSnapshot getMail() { return mail; }
    public Instant getObservedAt() { return observedAt; }
}
