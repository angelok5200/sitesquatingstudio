package org.tafel.squating.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "squating")
public class ApplicationProperties {
    private String defaultBrand = "sparda-bank.de";
    private int defaultScanIntervalHours = 168; // Weekly
    private int httpTimeoutMs = 5000;
    private List<String> monitoredTlds = List.of(".de", ".com", ".at", ".ch");
    private boolean autoScanOnStartup = true;

    public String getDefaultBrand() { return defaultBrand; }
    public void setDefaultBrand(String defaultBrand) { this.defaultBrand = defaultBrand; }

    public int getDefaultScanIntervalHours() { return defaultScanIntervalHours; }
    public void setDefaultScanIntervalHours(int defaultScanIntervalHours) { this.defaultScanIntervalHours = defaultScanIntervalHours; }

    public int getHttpTimeoutMs() { return httpTimeoutMs; }
    public void setHttpTimeoutMs(int httpTimeoutMs) { this.httpTimeoutMs = httpTimeoutMs; }

    public List<String> getMonitoredTlds() { return monitoredTlds; }
    public void setMonitoredTlds(List<String> monitoredTlds) { this.monitoredTlds = monitoredTlds; }

    public boolean isAutoScanOnStartup() { return autoScanOnStartup; }
    public void setAutoScanOnStartup(boolean autoScanOnStartup) { this.autoScanOnStartup = autoScanOnStartup; }
}
