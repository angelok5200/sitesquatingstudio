package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.model.Alert;

import java.util.List;
import java.util.Optional;

public interface AlertRepository {
    Alert save(Alert alert);
    Optional<Alert> findById(String id);
    List<Alert> findPendingByBrandId(String brandId);
    List<Alert> findAllByBrandId(String brandId);
    void acknowledge(String alertId);
}
