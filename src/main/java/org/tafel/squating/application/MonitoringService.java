package org.tafel.squating.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tafel.squating.domain.model.*;
import org.tafel.squating.domain.value.MonitoringPolicy;
import org.tafel.squating.ports.inbound.EvaluateRiskUseCase;
import org.tafel.squating.ports.inbound.MonitorDomainUseCase;
import org.tafel.squating.ports.inbound.ScanCandidatesUseCase;
import org.tafel.squating.ports.outbound.AlertRepository;
import org.tafel.squating.ports.outbound.BrandRepository;
import org.tafel.squating.ports.outbound.CandidateRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MonitoringService implements MonitorDomainUseCase, ScanCandidatesUseCase, EvaluateRiskUseCase {

    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);

    private final BrandRepository brandRepository;
    private final CandidateRepository candidateRepository;
    private final AlertRepository alertRepository;
    private final CandidateGenerationService candidateGenerationService;
    private final RegistrationDiscoveryService registrationDiscoveryService;
    private final DomainInspectionService domainInspectionService;
    private final RiskAssessmentService riskAssessmentService;
    private final AlertDecisionService alertDecisionService;

    public MonitoringService(
        BrandRepository brandRepository,
        CandidateRepository candidateRepository,
        AlertRepository alertRepository,
        CandidateGenerationService candidateGenerationService,
        RegistrationDiscoveryService registrationDiscoveryService,
        DomainInspectionService domainInspectionService,
        RiskAssessmentService riskAssessmentService,
        AlertDecisionService alertDecisionService
    ) {
        this.brandRepository = brandRepository;
        this.candidateRepository = candidateRepository;
        this.alertRepository = alertRepository;
        this.candidateGenerationService = candidateGenerationService;
        this.registrationDiscoveryService = registrationDiscoveryService;
        this.domainInspectionService = domainInspectionService;
        this.riskAssessmentService = riskAssessmentService;
        this.alertDecisionService = alertDecisionService;
    }

    // --- Inbound MonitorDomainUseCase ---

    @Override
    public Brand registerBrand(String brandName, String primaryDomain, MonitoringPolicy policy) {
        Brand brand = new Brand(
            null,
            brandName,
            primaryDomain,
            null,
            null,
            policy != null ? policy : MonitoringPolicy.defaultGermanPolicy()
        );
        return brandRepository.save(brand);
    }

    @Override
    public Optional<Brand> findBrandById(String id) {
        return brandRepository.findById(id);
    }

    @Override
    public List<Brand> listBrands() {
        return brandRepository.findAll();
    }

    @Override
    public void updateBrandPolicy(String brandId, MonitoringPolicy policy) {
        brandRepository.findById(brandId).ifPresent(brand -> {
            brand.setPolicy(policy);
            brandRepository.save(brand);
        });
    }

    // --- Inbound ScanCandidatesUseCase ---

    @Override
    public List<CandidateDomain> generateCandidates(String brandId) {
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));
        return candidateGenerationService.generateCandidatesForBrand(brand);
    }

    @Override
    public List<CandidateDomain> scanActiveCandidates(String brandId) {
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));

        List<CandidateDomain> candidates = candidateRepository.findByBrandId(brandId);
        if (candidates.isEmpty()) {
            candidates = candidateGenerationService.generateCandidatesForBrand(brand);
        }

        for (CandidateDomain candidate : candidates) {
            registrationDiscoveryService.discoverRegistration(candidate);
            DomainObservation observation = domainInspectionService.inspectDomain(candidate);
            RiskAssessment assessment = riskAssessmentService.assessCandidateRisk(candidate, observation);
            alertDecisionService.evaluateAlert(brand, candidate, assessment);
        }

        brand.setLastAuditedAt(Instant.now());
        brandRepository.save(brand);
        return candidateRepository.findByBrandId(brandId);
    }

    @Override
    public List<CandidateDomain> getCandidatesForBrand(String brandId) {
        return candidateRepository.findByBrandId(brandId);
    }

    // --- Inbound EvaluateRiskUseCase ---

    @Override
    public RiskAssessment evaluateCandidate(CandidateDomain candidate) {
        DomainObservation observation = domainInspectionService.inspectDomain(candidate);
        return riskAssessmentService.assessCandidateRisk(candidate, observation);
    }

    @Override
    public List<Alert> evaluateBrandRisksAndAlert(String brandId) {
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));

        List<Alert> newAlerts = new ArrayList<>();
        List<CandidateDomain> candidates = candidateRepository.findByBrandId(brandId);
        for (CandidateDomain c : candidates) {
            DomainObservation observation = domainInspectionService.inspectDomain(c);
            RiskAssessment assessment = riskAssessmentService.assessCandidateRisk(c, observation);
            alertDecisionService.evaluateAlert(brand, c, assessment).ifPresent(newAlerts::add);
        }
        return newAlerts;
    }

    @Override
    public List<Alert> getPendingAlerts(String brandId) {
        return alertRepository.findPendingByBrandId(brandId);
    }

    // --- Automated Scheduled Surveillance (e.g. Weekly) ---
    @Scheduled(cron = "0 0 3 * * MON")
    public void runWeeklySurveillanceAudit() {
        log.info("⏰ Executing scheduled weekly domain squatting audit across all monitored brands...");
        for (Brand brand : brandRepository.findAll()) {
            try {
                scanActiveCandidates(brand.getId());
            } catch (Exception e) {
                log.error("Failed surveillance scan for brand: {}", brand.getName(), e);
            }
        }
    }
}
