package org.tafel.squating.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tafel.squating.domain.enums.CandidateStatus;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.enums.RiskLevel;
import org.tafel.squating.domain.model.Alert;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.domain.value.MonitoringPolicy;
import org.tafel.squating.ports.inbound.MonitorDomainUseCase;
import org.tafel.squating.ports.inbound.ScanCandidatesUseCase;
import org.tafel.squating.ports.outbound.AlertRepository;
import org.tafel.squating.ports.outbound.CandidateRepository;

import java.time.Instant;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MonitorDomainUseCase monitorDomainUseCase;
    private final ScanCandidatesUseCase scanCandidatesUseCase;
    private final CandidateRepository candidateRepository;
    private final AlertRepository alertRepository;

    public DataInitializer(
        MonitorDomainUseCase monitorDomainUseCase,
        ScanCandidatesUseCase scanCandidatesUseCase,
        CandidateRepository candidateRepository,
        AlertRepository alertRepository
    ) {
        this.monitorDomainUseCase = monitorDomainUseCase;
        this.scanCandidatesUseCase = scanCandidatesUseCase;
        this.candidateRepository = candidateRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing German Sitesquatting Monitor reference brands...");

        // 1. Seed Tafel Deutschland
        Brand tafel = monitorDomainUseCase.registerBrand(
            "Tafel Deutschland",
            "tafel.de",
            MonitoringPolicy.defaultGermanPolicy()
        );
        tafel.setIndustrySector("Non-Profit & Humanitarian");

        // 2. Seed Sparda-Bank
        Brand sparda = monitorDomainUseCase.registerBrand(
            "Sparda-Bank",
            "sparda-bank.de",
            MonitoringPolicy.defaultGermanPolicy()
        );
        sparda.setIndustrySector("Banking & Financial Services");

        // Generate candidates for both brands
        List<CandidateDomain> tafelCandidates = scanCandidatesUseCase.generateCandidates(tafel.getId());
        List<CandidateDomain> spardaCandidates = scanCandidatesUseCase.generateCandidates(sparda.getId());

        log.info("Generated {} candidates for {} and {} candidates for {}",
            tafelCandidates.size(), tafel.getName(), spardaCandidates.size(), sparda.getName());

        // Add realistic active squatting threats to demonstrate detection capabilities
        seedThreatScenario(sparda);
        seedThreatScenario(tafel);
    }

    private void seedThreatScenario(Brand brand) {
        if ("sparda-bank.de".equalsIgnoreCase(brand.getPrimaryDomain())) {
            CandidateDomain threat = new CandidateDomain(
                null,
                brand.getId(),
                "sparda-login.de",
                brand.getPrimaryDomain(),
                MutationType.COMBOSQUATTING,
                "Combosquatting with high-urgency operational keyword '-login'"
            );
            threat.setStatus(CandidateStatus.ACTIVE_MAIL);
            threat.setRiskScore(92);
            threat.setRiskLevel(RiskLevel.CRITICAL);
            threat.setPhoneticSimilarity(0.95);
            threat.setVisualSimilarity(0.88);
            threat.setCologneCode("8172");
            threat.setLastObservedAt(Instant.now());
            candidateRepository.save(threat);

            Alert alert = new Alert(
                null,
                brand.getId(),
                threat.getId(),
                threat.getDomainName(),
                org.tafel.squating.domain.enums.AlertType.MX_RECORDS_ACTIVATED,
                RiskLevel.CRITICAL,
                "CRITICAL: Active MX Records on Combosquatted Banking Domain",
                "Domain 'sparda-login.de' has live MX mail exchanges configured without SPF/DMARC alignment. Severe risk of CEO fraud & customer credential phishing."
            );
            alertRepository.save(alert);
        } else if ("tafel.de".equalsIgnoreCase(brand.getPrimaryDomain())) {
            CandidateDomain typo = new CandidateDomain(
                null,
                brand.getId(),
                "tafle.de",
                brand.getPrimaryDomain(),
                MutationType.TRANSPOSITION,
                "Transposed adjacent letters 'e' and 'l'"
            );
            typo.setStatus(CandidateStatus.ACTIVE_WEB);
            typo.setRiskScore(75);
            typo.setRiskLevel(RiskLevel.HIGH);
            typo.setPhoneticSimilarity(1.0);
            typo.setVisualSimilarity(0.80);
            typo.setCologneCode("235");
            typo.setLastObservedAt(Instant.now());
            candidateRepository.save(typo);

            Alert alert = new Alert(
                null,
                brand.getId(),
                typo.getId(),
                typo.getDomainName(),
                org.tafel.squating.domain.enums.AlertType.HIGH_RISK_SQUATTING,
                RiskLevel.HIGH,
                "HIGH: Kölner Phonetik match and active web server on 'tafle.de'",
                "Transposed candidate 'tafle.de' sounds identical to 'tafel.de' (Cologne Code: 235) and responds with live HTTP 200."
            );
            alertRepository.save(alert);
        }
    }
}
