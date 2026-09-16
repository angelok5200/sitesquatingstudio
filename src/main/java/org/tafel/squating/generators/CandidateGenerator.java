package org.tafel.squating.generators;

import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;

import java.util.List;

public interface CandidateGenerator {
    List<CandidateDomain> generate(Brand brand);
    MutationType getMutationType();
}
