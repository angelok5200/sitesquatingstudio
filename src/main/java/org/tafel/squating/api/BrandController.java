package org.tafel.squating.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafel.squating.api.dto.BrandRequestDto;
import org.tafel.squating.api.dto.BrandResponseDto;
import org.tafel.squating.api.mappers.DomainDtoMapper;
import org.tafel.squating.domain.enums.RiskLevel;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.value.MonitoringPolicy;
import org.tafel.squating.ports.inbound.MonitorDomainUseCase;
import org.tafel.squating.ports.outbound.AlertRepository;
import org.tafel.squating.ports.outbound.CandidateRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "*")
public class BrandController {

    private final MonitorDomainUseCase monitorDomainUseCase;
    private final CandidateRepository candidateRepository;
    private final AlertRepository alertRepository;
    private final DomainDtoMapper mapper;

    public BrandController(
        MonitorDomainUseCase monitorDomainUseCase,
        CandidateRepository candidateRepository,
        AlertRepository alertRepository,
        DomainDtoMapper mapper
    ) {
        this.monitorDomainUseCase = monitorDomainUseCase;
        this.candidateRepository = candidateRepository;
        this.alertRepository = alertRepository;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<BrandResponseDto> registerBrand(@RequestBody BrandRequestDto request) {
        MonitoringPolicy policy = MonitoringPolicy.defaultGermanPolicy();
        if (request.alertThreshold() != null) {
            policy = new MonitoringPolicy(
                policy.scanIntervalHours(),
                policy.maxCandidatesPerRun(),
                request.alertThreshold(),
                policy.checkMxRecords(),
                policy.checkHttpContent(),
                policy.checkCertificates(),
                policy.enabledMutations()
            );
        }

        Brand brand = monitorDomainUseCase.registerBrand(request.name(), request.primaryDomain(), policy);
        if (request.industrySector() != null) {
            brand.setIndustrySector(request.industrySector());
        }

        return ResponseEntity.ok(mapper.toBrandResponse(brand, 0, 0));
    }

    @GetMapping
    public ResponseEntity<List<BrandResponseDto>> listBrands() {
        List<BrandResponseDto> list = monitorDomainUseCase.listBrands().stream()
            .map(b -> {
                long candidates = candidateRepository.findByBrandId(b.getId()).size();
                long criticalAlerts = alertRepository.findPendingByBrandId(b.getId()).stream()
                    .filter(a -> a.getSeverity() == RiskLevel.CRITICAL)
                    .count();
                return mapper.toBrandResponse(b, candidates, criticalAlerts);
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponseDto> getBrand(@PathVariable String id) {
        return monitorDomainUseCase.findBrandById(id)
            .map(b -> {
                long candidates = candidateRepository.findByBrandId(b.getId()).size();
                long criticalAlerts = alertRepository.findPendingByBrandId(b.getId()).stream()
                    .filter(a -> a.getSeverity() == RiskLevel.CRITICAL)
                    .count();
                return ResponseEntity.ok(mapper.toBrandResponse(b, candidates, criticalAlerts));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
