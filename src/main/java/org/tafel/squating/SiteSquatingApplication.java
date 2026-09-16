package org.tafel.squating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.tafel.squating.config.ApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class SiteSquatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiteSquatingApplication.class, args);
    }
}
