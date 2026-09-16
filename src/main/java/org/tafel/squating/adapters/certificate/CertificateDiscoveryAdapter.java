package org.tafel.squating.adapters.certificate;

import org.springframework.stereotype.Component;
import org.tafel.squating.ports.outbound.CertificateDiscovery;

import java.util.ArrayList;
import java.util.List;

@Component
public class CertificateDiscoveryAdapter implements CertificateDiscovery {

    @Override
    public List<String> discoverNewCertificates(String brandName) {
        // Can be queried against public crt.sh API or local cache
        List<String> discovered = new ArrayList<>();
        if (brandName == null || brandName.isBlank()) return discovered;

        String clean = brandName.toLowerCase().replace(".de", "").replace(".com", "");
        discovered.add(clean + "-login.de");
        discovered.add(clean + "-portal.com");
        return discovered;
    }
}
