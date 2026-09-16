package org.tafel.squating.generators;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;

import java.util.ArrayList;
import java.util.List;

@Component
public class TranspositionGenerator implements CandidateGenerator {

    @Override
    public List<CandidateDomain> generate(Brand brand) {
        List<CandidateDomain> result = new ArrayList<>();
        String domain = brand.getPrimaryDomain();
        int dot = domain.indexOf('.');
        if (dot <= 0) return result;

        String name = domain.substring(0, dot);
        String tld = domain.substring(dot);

        for (int i = 0; i < name.length() - 1; i++) {
            if (name.charAt(i) == name.charAt(i + 1)) continue;
            char[] chars = name.toCharArray();
            char temp = chars[i];
            chars[i] = chars[i + 1];
            chars[i + 1] = temp;

            String mutated = new String(chars);
            String candidateName = mutated + tld;
            result.add(new CandidateDomain(
                null,
                brand.getId(),
                candidateName,
                domain,
                MutationType.TRANSPOSITION,
                "Transposed adjacent letters '" + name.charAt(i) + "' and '" + name.charAt(i + 1) + "'"
            ));
        }

        return result;
    }

    @Override
    public MutationType getMutationType() {
        return MutationType.TRANSPOSITION;
    }
}
