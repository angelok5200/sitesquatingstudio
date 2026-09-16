package org.tafel.squating.analysis;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.enums.ContentIndicator;
import org.tafel.squating.domain.value.HttpSnapshot;
import org.tafel.squating.ports.outbound.ContentAnalyser;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContentAnalyserImpl implements ContentAnalyser {

    @Override
    public ContentAnalysis analyze(String brandName, HttpSnapshot httpSnapshot, String rawHtml) {
        if (httpSnapshot == null || !httpSnapshot.isLive() || rawHtml == null || rawHtml.isBlank()) {
            return ContentAnalysis.clean();
        }

        List<ContentIndicator> indicators = new ArrayList<>();
        List<String> matchedSignatures = new ArrayList<>();
        int score = 0;

        boolean isParked = ContentRules.PARKED_PAGE_PATTERN.matcher(rawHtml).find();
        if (isParked) {
            indicators.add(ContentIndicator.PARKED_DOMAIN_PAGE);
            matchedSignatures.add("Domain Parking Signatures matched");
            return new ContentAnalysis(false, true, 10, indicators, matchedSignatures, "Domain appears parked or on sale.");
        }

        if (ContentRules.LOGIN_FORM_PATTERN.matcher(rawHtml).find()) {
            indicators.add(ContentIndicator.LOGIN_FORM);
            indicators.add(ContentIndicator.PASSWORD_FIELD);
            matchedSignatures.add("HTML Password/Credential Login Form Present");
            score += 45;
        }

        if (ContentRules.PHISHING_KIT_PATTERN.matcher(rawHtml).find()) {
            indicators.add(ContentIndicator.PHISHING_KIT_MARKER);
            matchedSignatures.add("High-urgency Phishing Kit Vocabulary");
            score += 40;
        }

        if (brandName != null && rawHtml.toLowerCase().contains(brandName.toLowerCase())) {
            matchedSignatures.add("Brand Name keyword present in HTML DOM: " + brandName);
            score += 20;
        }

        boolean isPhishingSuspicious = score >= 50;

        return new ContentAnalysis(
            isPhishingSuspicious,
            false,
            Math.min(100, score),
            indicators,
            matchedSignatures,
            isPhishingSuspicious ? "Critical credential phishing risk detected in web payload." : "Web page active without immediate phishing signatures."
        );
    }
}
