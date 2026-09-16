package org.tafel.squating.generators;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubstitutionGenerator implements CandidateGenerator {

    private static final String[][] HOMOGLYPHS = {
        {"rn", "m"}, {"m", "rn"},
        {"vv", "w"}, {"w", "vv"},
        {"cl", "d"}, {"d", "cl"},
        {"l", "1"}, {"1", "l"},
        {"o", "0"}, {"0", "o"}
    };

    private static final String[][] UMLAUT_REPLACEMENTS = {
        {"ae", "a"}, {"a", "ae"},
        {"oe", "o"}, {"o", "oe"},
        {"ue", "u"}, {"u", "ue"},
        {"ss", "s"}, {"s", "ss"}
    };

    private static final String[] COMBOSQUAT_KEYWORDS = {
        "-login", "-portal", "-online", "-banking", "-konto", "-sicherheit", "-service"
    };

    private static final String[] DACH_TLDS = {
        ".de", ".com", ".at", ".ch", ".net", ".org", ".info"
    };

    @Override
    public List<CandidateDomain> generate(Brand brand) {
        List<CandidateDomain> result = new ArrayList<>();
        String domain = brand.getPrimaryDomain();
        int dot = domain.indexOf('.');
        if (dot <= 0) return result;

        String name = domain.substring(0, dot);
        String currentTld = domain.substring(dot);

        // 1. Homoglyphs
        for (String[] pair : HOMOGLYPHS) {
            String from = pair[0];
            String to = pair[1];
            if (name.contains(from)) {
                String mutated = name.replaceFirst(from, to);
                result.add(new CandidateDomain(
                    null,
                    brand.getId(),
                    mutated + currentTld,
                    domain,
                    MutationType.HOMOGLYPH,
                    "Visual homoglyph substitution: '" + from + "' -> '" + to + "'"
                ));
            }
        }

        // 2. German Umlaut mutations
        for (String[] pair : UMLAUT_REPLACEMENTS) {
            String from = pair[0];
            String to = pair[1];
            if (name.contains(from)) {
                String mutated = name.replaceFirst(from, to);
                result.add(new CandidateDomain(
                    null,
                    brand.getId(),
                    mutated + currentTld,
                    domain,
                    MutationType.UMLAUT,
                    "German Umlaut/vowel mutation: '" + from + "' -> '" + to + "'"
                ));
            }
        }

        // 3. Combosquatting for German financial/corporate services
        for (String kw : COMBOSQUAT_KEYWORDS) {
            result.add(new CandidateDomain(
                null,
                brand.getId(),
                name + kw + currentTld,
                domain,
                MutationType.COMBOSQUATTING,
                "Combosquatting keyword append: '" + kw + "'"
            ));
        }

        // 4. DACH TLD Cross-Swap
        for (String tld : DACH_TLDS) {
            if (!tld.equalsIgnoreCase(currentTld)) {
                result.add(new CandidateDomain(
                    null,
                    brand.getId(),
                    name + tld,
                    domain,
                    MutationType.TLD_SWAP,
                    "DACH TLD swap: '" + currentTld + "' -> '" + tld + "'"
                ));
            }
        }

        return result;
    }

    @Override
    public MutationType getMutationType() {
        return MutationType.SUBSTITUTION;
    }
}
