package org.tafel.squating.application;

import org.springframework.stereotype.Service;
import org.tafel.squating.domain.enums.AlertType;
import org.tafel.squating.domain.enums.RiskLevel;
import org.tafel.squating.domain.model.Alert;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.domain.model.RiskAssessment;
import org.tafel.squating.ports.outbound.AlertRepository;
import org.tafel.squating.ports.outbound.AlertSender;

import java.util.Optional;

@Service
public class AlertDecisionService {

    private final AlertRepository alertRepository;
    private final AlertSender alertSender;

    public AlertDecisionService(AlertRepository alertRepository, AlertSender alertSender) {
        this.alertRepository = alertRepository;
        this.alertSender = alertSender;
    }

    public Optional<Alert> evaluateAlert(Brand brand, CandidateDomain candidate, RiskAssessment assessment) {
        int threshold = brand.getPolicy().alertRiskThreshold();

        if (assessment.getTotalScore() >= threshold || assessment.getRiskLevel() == RiskLevel.CRITICAL) {
            AlertType type = AlertType.HIGH_RISK_SQUATTING;
            if (assessment.getPrimaryVector().contains("Email")) {
                type = AlertType.MX_RECORDS_ACTIVATED;
            } else if (assessment.getPrimaryVector().contains("Login") || assessment.getPrimaryVector().contains("Credential")) {
                type = AlertType.WEB_LOGIN_DETECTED;
            }

            String headline = "Squatting Threat Detected: " + candidate.getDomainName() + " (Score: " + assessment.getTotalScore() + "/100)";
            String message = String.format(
                "Domain %s targeting %s triggered high alert rules: %s. Primary Vector: %s",
                candidate.getDomainName(),
                brand.getName(),
                String.join("; ", assessment.getTriggeredRuleDescriptions()),
                assessment.getPrimaryVector()
            );

            Alert alert = new Alert(
                null,
                brand.getId(),
                candidate.getId(),
                candidate.getDomainName(),
                type,
                assessment.getRiskLevel(),
                headline,
                message
            );

            alertRepository.save(alert);
            alertSender.sendAlert(alert);
            return Optional.of(alert);
        }

        return Optional.empty();
    }
}
