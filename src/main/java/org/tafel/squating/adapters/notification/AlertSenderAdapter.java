package org.tafel.squating.adapters.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tafel.squating.domain.model.Alert;
import org.tafel.squating.ports.outbound.AlertSender;

@Component
public class AlertSenderAdapter implements AlertSender {

    private static final Logger log = LoggerFactory.getLogger(AlertSenderAdapter.class);

    @Override
    public void sendAlert(Alert alert) {
        log.warn("🚨 [SITE-SQUATING ALERT] [{} - {}] Domain: {} | Severity: {} | {}",
            alert.getType(),
            alert.getHeadline(),
            alert.getDomainName(),
            alert.getSeverity(),
            alert.getMessage()
        );
    }
}
