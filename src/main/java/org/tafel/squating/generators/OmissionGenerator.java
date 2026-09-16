package org.tafel.squating.generators;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;

import java.util.ArrayList;
import java.util.List;

@Component
public class OmissionGenerator implements CandidateGenerator {

    @Override
    public List<CandidateDomain> generate(Brand brand) {
        List<CandidateDomain> result = new ArrayList<>();
        String domain = brand.getPrimaryDomain();
        int dot = domain.indexOf('.');
        if (dot <= 0) return result;

        String name = domain.substring(0, dot);
        String tld = domain.substring(dot);

        for (int i = 0; i < name.length(); i++) {
            if (name.length() <= 3) break;
            String mutated = name.substring(0, i) + name.substring(i + 1);
            if (!mutated.equals(name)) {
                String candidateName = mutated + tld;
                result.add(new CandidateDomain(
                    null,
                    brand.getId(),
                    candidateName,
                    domain,
                    MutationType.OMISSION,
                    "Omission of character '" + name.charAt(i) + "' at position " + (i + 1)
                ));
            }
        }

        return result;
    }

    @Override
    public MutationType getMutationType() {
        return MutationType.OMISSION;
    }
}
