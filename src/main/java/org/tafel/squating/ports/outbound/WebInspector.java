package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.value.HttpSnapshot;

public interface WebInspector {
    HttpSnapshot inspectWeb(String domainName);
}
