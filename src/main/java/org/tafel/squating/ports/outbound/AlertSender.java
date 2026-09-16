package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.model.Alert;

public interface AlertSender {
    void sendAlert(Alert alert);
}
