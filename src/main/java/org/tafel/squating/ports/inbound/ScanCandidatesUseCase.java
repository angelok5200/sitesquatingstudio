package org.tafel.squating.ports.inbound;

import org.tafel.squating.domain.model.CandidateDomain;

import java.util.List;

public interface ScanCandidatesUseCase {
    List<CandidateDomain> generateCandidates(String brandId);
    List<CandidateDomain> scanActiveCandidates(String brandId);
    List<CandidateDomain> getCandidatesForBrand(String brandId);
}
