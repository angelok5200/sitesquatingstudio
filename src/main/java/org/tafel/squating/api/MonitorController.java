package org.tafel.squating.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafel.squating.api.dto.AlertDto;
import org.tafel.squating.api.dto.CandidateDomainDto;
import org.tafel.squating.api.dto.ScanSummaryDto;
import org.tafel.squating.api.mappers.DomainDtoMapper;
import org.tafel.squating.domain.enums.CandidateStatus;
import org.tafel.squating.domain.enums.RiskLevel;
import org.tafel.squating.domain.model.Alert;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.ports.inbound.EvaluateRiskUseCase;
import org.tafel.squating.ports.inbound.MonitorDomainUseCase;
import org.tafel.squating.ports.inbound.ScanCandidatesUseCase;
import org.tafel.squating.ports.outbound.AlertRepository;
import org.tafel.squating.ports.outbound.CandidateRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/monitor")
@CrossOrigin(origins = "*")
public class MonitorController {

    private final MonitorDomainUseCase monitorDomainUseCase;
    private final ScanCandidatesUseCase scanCandidatesUseCase;
    private final EvaluateRiskUseCase evaluateRiskUseCase;
    private final CandidateRepository candidateRepository;
    private final AlertRepository alertRepository;
    private final DomainDtoMapper mapper;

    public MonitorController(
        MonitorDomainUseCase monitorDomainUseCase,
        ScanCandidatesUseCase scanCandidatesUseCase,
        EvaluateRiskUseCase evaluateRiskUseCase,
        CandidateRepository candidateRepository,
        AlertRepository alertRepository,
        DomainDtoMapper mapper
    ) {
        this.monitorDomainUseCase = monitorDomainUseCase;
        this.scanCandidatesUseCase = scanCandidatesUseCase;
        this.evaluateRiskUseCase = evaluateRiskUseCase;
        this.candidateRepository = candidateRepository;
        this.alertRepository = alertRepository;
        this.mapper = mapper;
    }

    @PostMapping("/{brandId}/generate")
    public ResponseEntity<List<CandidateDomainDto>> generateCandidates(@PathVariable String brandId) {
        List<CandidateDomain> list = scanCandidatesUseCase.generateCandidates(brandId);
        List<CandidateDomainDto> dtos = list.stream()
            .map(mapper::toCandidateDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{brandId}/scan")
    public ResponseEntity<ScanSummaryDto> triggerScan(@PathVariable String brandId) {
        Brand brand = monitorDomainUseCase.findBrandById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));

        List<CandidateDomain> scanned = scanCandidatesUseCase.scanActiveCandidates(brandId);

        int registered = 0;
        int activeWeb = 0;
        int activeMail = 0;
        int crit = 0;
        int high = 0;
        int med = 0;
        int safe = 0;
        Map<String, Integer> mutations = new HashMap<>();

        for (CandidateDomain c : scanned) {
            if (c.getStatus() != CandidateStatus.UNREGISTERED && c.getStatus() != CandidateStatus.GENERATED) {
                registered++;
            }
            if (c.getStatus() == CandidateStatus.ACTIVE_WEB || c.getStatus() == CandidateStatus.SUSPECTED_PHISHING) {
                activeWeb++;
            }
            if (c.getStatus() == CandidateStatus.ACTIVE_MAIL) {
                activeMail++;
            }

            if (c.getRiskLevel() == RiskLevel.CRITICAL) crit++;
            else if (c.getRiskLevel() == RiskLevel.HIGH) high++;
            else if (c.getRiskLevel() == RiskLevel.MEDIUM) med++;
            else safe++;

            mutations.merge(c.getMutationType().name(), 1, Integer::sum);
        }

        ScanSummaryDto summary = new ScanSummaryDto(
            brand.getId(),
            brand.getName(),
            scanned.size(),
            registered,
            activeWeb,
            activeMail,
            crit,
            high,
            med,
            safe,
            Instant.now(),
            mutations
        );

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{brandId}/candidates")
    public ResponseEntity<List<CandidateDomainDto>> getCandidates(
        @PathVariable String brandId,
        @RequestParam(required = false) RiskLevel riskLevel,
        @RequestParam(required = false) CandidateStatus status
    ) {
        List<CandidateDomain> list = candidateRepository.findByBrandId(brandId);

        if (riskLevel != null) {
            list = list.stream().filter(c -> c.getRiskLevel() == riskLevel).collect(Collectors.toList());
        }
        if (status != null) {
            list = list.stream().filter(c -> c.getStatus() == status).collect(Collectors.toList());
        }

        // Sort by risk score descending
        list.sort((c1, c2) -> Integer.compare(c2.getRiskScore(), c1.getRiskScore()));

        List<CandidateDomainDto> dtos = list.stream()
            .map(mapper::toCandidateDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{brandId}/alerts")
    public ResponseEntity<List<AlertDto>> getAlerts(@PathVariable String brandId) {
        List<AlertDto> dtos = alertRepository.findAllByBrandId(brandId).stream()
            .map(mapper::toAlertDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/alerts/{alertId}/acknowledge")
    public ResponseEntity<Void> acknowledgeAlert(@PathVariable String alertId) {
        alertRepository.acknowledge(alertId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        List<Brand> brands = monitorDomainUseCase.listBrands();
        long totalCandidates = candidateRepository.count();
        long totalAlerts = alertRepository.findAllByBrandId("").size();

        return ResponseEntity.ok(Map.of(
            "monitoredBrandsCount", brands.size(),
            "totalPermutationsMonitored", totalCandidates,
            "systemStatus", "OPERATIONAL",
            "activeModule", "German Sitesquatting Monitor (Hexagonal)"
        ));
    }
}
