package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.value.MailSnapshot;

public interface MailInspector {
    MailSnapshot inspectMail(String domainName);
}
