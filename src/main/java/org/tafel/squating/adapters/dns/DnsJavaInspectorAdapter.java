package org.tafel.squating.adapters.dns;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.value.DnsSnapshot;
import org.tafel.squating.ports.outbound.DnsInspector;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import java.net.InetAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DnsJavaInspectorAdapter implements DnsInspector {

    @Override
    public DnsSnapshot inspectDns(String domainName) {
        List<String> aRecords = new ArrayList<>();
        List<String> mxRecords = new ArrayList<>();
        List<String> nsRecords = new ArrayList<>();
        List<String> txtRecords = new ArrayList<>();

        // 1. Resolve A / IPv4 records
        try {
            InetAddress[] addresses = InetAddress.getAllByName(domainName);
            for (InetAddress addr : addresses) {
                aRecords.add(addr.getHostAddress());
            }
        } catch (Exception ignored) {
        }

        // 2. Resolve MX records using dnsjava
        try {
            Lookup lookup = new Lookup(domainName, Type.MX);
            Record[] records = lookup.run();
            if (records != null) {
                for (Record r : records) {
                    if (r instanceof MXRecord mx) {
                        mxRecords.add(mx.getTarget().toString(true) + " (priority: " + mx.getPriority() + ")");
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // 3. Fallback JNDI or simulated MX if known
        boolean hasDns = !aRecords.isEmpty() || !mxRecords.isEmpty();

        return new DnsSnapshot(
            hasDns,
            aRecords,
            List.of(),
            mxRecords,
            nsRecords,
            txtRecords,
            Instant.now()
        );
    }
}
