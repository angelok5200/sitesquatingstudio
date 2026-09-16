package org.tafel.squating.api.dto;

import java.util.List;

public record BrandRequestDto(
    String name,
    String primaryDomain,
    String industrySector,
    List<String> tldsToMonitor,
    Integer alertThreshold
) {
}
