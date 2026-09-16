package org.tafel.squating.generators;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;

import java.util.ArrayList;
import java.util.List;

@Component
public class DuplicationGenerator implements CandidateGenerator {

    @Override
    public List<CandidateDomain> generate(Brand brand) {
        List<CandidateDomain> result = new ArrayList<>();
        String domain = brand.getPrimaryDomain();
        int dot = domain.indexOf('.');
        if (dot <= 0) return result;

        String name = domain.substring(0, dot);
        String tld = domain.substring(dot);

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '-' || c == '.') continue;
            String mutated = name.substring(0, i) + c + name.substring(i);
            String candidateName = mutated + tld;
            result.add(new CandidateDomain(
                null,
                brand.getId(),
                candidateName,
                domain,
                MutationType.DUPLICATION,
                "Duplicated letter '" + c + "' at position " + (i + 1)
            ));
        }

        return result;
    }

    @Override
    public MutationType getMutationType() {
        return MutationType.DUPLICATION;
    }
}
