package org.tafel.squating.domain.value;

import java.time.Instant;
import java.util.List;

public record DnsSnapshot(
    boolean hasDns,
    List<String> aRecords,
    List<String> aaaaRecords,
    List<String> mxRecords,
    List<String> nsRecords,
    List<String> txtRecords,
    Instant resolvedAt
) {
    public static DnsSnapshot empty() {
        return new DnsSnapshot(false, List.of(), List.of(), List.of(), List.of(), List.of(), Instant.now());
    }
}
