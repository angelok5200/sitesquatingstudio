package org.tafel.squating.domain.value;

import java.time.Instant;
import java.util.List;

public record TlsSnapshot(
    boolean hasCertificate,
    String subjectCommonName,
    String issuerCommonName,
    Instant validFrom,
    Instant validTo,
    List<String> subjectAltNames,
    boolean isSelfSigned
) {
    public static TlsSnapshot none() {
        return new TlsSnapshot(false, null, null, null, null, List.of(), false);
    }
}
