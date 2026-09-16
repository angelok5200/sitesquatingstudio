package org.tafel.squating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient domainHttpClient(ApplicationProperties properties) {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofMillis(properties.getHttpTimeoutMs()))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }
}
