package org.tafel.squating.domain.value;

import java.time.Instant;
import java.util.List;

public record RegistrationSnapshot(
    boolean isRegistered,
    String registrar,
    Instant creationDate,
    Instant expirationDate,
    List<String> nameServers,
    String status
) {
    public static RegistrationSnapshot unregistered() {
        return new RegistrationSnapshot(false, null, null, null, List.of(), "AVAILABLE");
    }
}
