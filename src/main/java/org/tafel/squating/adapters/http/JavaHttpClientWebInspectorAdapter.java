package org.tafel.squating.adapters.http;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.enums.ContentIndicator;
import org.tafel.squating.domain.value.HttpSnapshot;
import org.tafel.squating.ports.outbound.WebInspector;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JavaHttpClientWebInspectorAdapter implements WebInspector {

    private final HttpClient httpClient;
    private static final Pattern TITLE_PATTERN = Pattern.compile("(?i)<title>(.*?)</title>");

    public JavaHttpClientWebInspectorAdapter(HttpClient domainHttpClient) {
        this.httpClient = domainHttpClient;
    }

    @Override
    public HttpSnapshot inspectWeb(String domainName) {
        String url = "https://" + domainName;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(3))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            int status = response.statusCode();

            String title = "No Title";
            if (body != null) {
                Matcher m = TITLE_PATTERN.matcher(body);
                if (m.find()) {
                    title = m.group(1).trim();
                }
            }

            List<ContentIndicator> indicators = new ArrayList<>();
            if (body != null && body.toLowerCase().contains("type=\"password\"")) {
                indicators.add(ContentIndicator.LOGIN_FORM);
                indicators.add(ContentIndicator.PASSWORD_FIELD);
            }

            return new HttpSnapshot(
                true,
                status,
                title,
                url,
                body != null ? body.length() : 0,
                indicators
            );
        } catch (Exception ignored) {
            // Try HTTP fallback
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + domainName))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return new HttpSnapshot(true, response.statusCode(), "HTTP Port Open", "http://" + domainName, 0, List.of());
            } catch (Exception e) {
                return HttpSnapshot.unreachable();
            }
        }
    }
}
