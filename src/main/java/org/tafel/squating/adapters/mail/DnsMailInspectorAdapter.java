package org.tafel.squating.adapters.mail;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.value.MailSnapshot;
import org.tafel.squating.ports.outbound.MailInspector;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import java.util.ArrayList;
import java.util.List;

@Component
public class DnsMailInspectorAdapter implements MailInspector {

    @Override
    public MailSnapshot inspectMail(String domainName) {
        List<String> mxHosts = new ArrayList<>();
        boolean hasSpf = false;
        boolean hasDmarc = false;

        try {
            Lookup lookup = new Lookup(domainName, Type.MX);
            Record[] records = lookup.run();
            if (records != null && records.length > 0) {
                for (Record r : records) {
                    if (r instanceof MXRecord mx) {
                        mxHosts.add(mx.getTarget().toString(true));
                    }
                }
            }
        } catch (Exception ignored) {}

        // Check SPF TXT record
        try {
            Lookup txtLookup = new Lookup(domainName, Type.TXT);
            Record[] txtRecords = txtLookup.run();
            if (txtRecords != null) {
                for (Record r : txtRecords) {
                    if (r.rdataToString().toLowerCase().contains("v=spf1")) {
                        hasSpf = true;
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}

        // Check DMARC
        try {
            Lookup dmarcLookup = new Lookup("_dmarc." + domainName, Type.TXT);
            Record[] dmarcRecords = dmarcLookup.run();
            if (dmarcRecords != null && dmarcRecords.length > 0) {
                hasDmarc = true;
            }
        } catch (Exception ignored) {}

        boolean hasMx = !mxHosts.isEmpty();
        boolean isMailableThreat = hasMx;

        return new MailSnapshot(
            hasMx,
            mxHosts,
            hasSpf,
            hasDmarc,
            isMailableThreat
        );
    }
}
