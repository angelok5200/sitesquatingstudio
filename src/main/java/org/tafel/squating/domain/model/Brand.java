package org.tafel.squating.domain.model;

import org.tafel.squating.domain.value.MonitoringPolicy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain Aggregate Root representing a protected Brand.
 */
public class Brand {
    private final String id;
    private String name;
    private String primaryDomain;
    private List<String> monitoredTlds;
    private List<String> highValueKeywords;
    private String industrySector;
    private MonitoringPolicy policy;
    private Instant createdAt;
    private Instant lastAuditedAt;

    public Brand(String id, String name, String primaryDomain, List<String> monitoredTlds, List<String> highValueKeywords, MonitoringPolicy policy) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.primaryDomain = primaryDomain.toLowerCase().trim();
        this.monitoredTlds = monitoredTlds != null ? monitoredTlds : List.of(".de", ".com", ".at", ".ch");
        this.highValueKeywords = highValueKeywords != null ? highValueKeywords : List.of("login", "online", "portal", "konto", "sicherheit");
        this.industrySector = "Corporate";
        this.policy = policy != null ? policy : MonitoringPolicy.defaultGermanPolicy();
        this.createdAt = Instant.now();
    }

    public static Brand of(String name, String domain) {
        return new Brand(null, name, domain, null, null, null);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPrimaryDomain() { return primaryDomain; }
    public List<String> getMonitoredTlds() { return monitoredTlds; }
    public List<String> getHighValueKeywords() { return highValueKeywords; }
    public String getIndustrySector() { return industrySector; }
    public MonitoringPolicy getPolicy() { return policy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastAuditedAt() { return lastAuditedAt; }

    public void setIndustrySector(String industrySector) { this.industrySector = industrySector; }
    public void setLastAuditedAt(Instant lastAuditedAt) { this.lastAuditedAt = lastAuditedAt; }
    public void setPolicy(MonitoringPolicy policy) { this.policy = policy; }
}
