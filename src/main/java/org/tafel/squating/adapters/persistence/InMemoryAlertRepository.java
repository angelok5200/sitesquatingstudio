package org.tafel.squating.adapters.persistence;

import org.springframework.stereotype.Repository;
import org.tafel.squating.domain.model.Alert;
import org.tafel.squating.ports.outbound.AlertRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryAlertRepository implements AlertRepository {

    private final Map<String, Alert> alerts = new ConcurrentHashMap<>();

    @Override
    public Alert save(Alert alert) {
        alerts.put(alert.getId(), alert);
        return alert;
    }

    @Override
    public Optional<Alert> findById(String id) {
        return Optional.ofNullable(alerts.get(id));
    }

    @Override
    public List<Alert> findPendingByBrandId(String brandId) {
        return alerts.values().stream()
            .filter(a -> a.getBrandId().equals(brandId) && !a.isAcknowledged())
            .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findAllByBrandId(String brandId) {
        return alerts.values().stream()
            .filter(a -> a.getBrandId().equals(brandId))
            .collect(Collectors.toList());
    }

    @Override
    public void acknowledge(String alertId) {
        Alert a = alerts.get(alertId);
        if (a != null) {
            a.setAcknowledged(true);
        }
    }
}
