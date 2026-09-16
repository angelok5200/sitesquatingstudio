package org.tafel.squating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tafel.squating.domain.value.MonitoringPolicy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class MonitorConfig {

    @Bean
    public MonitoringPolicy defaultMonitoringPolicy() {
        return MonitoringPolicy.defaultGermanPolicy();
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService domainInspectionExecutor() {
        return Executors.newFixedThreadPool(8);
    }
}
