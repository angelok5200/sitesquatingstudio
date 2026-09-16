package org.tafel.squating.ports.outbound;

import java.util.List;

public interface CertificateDiscovery {
    List<String> discoverNewCertificates(String brandName);
}
