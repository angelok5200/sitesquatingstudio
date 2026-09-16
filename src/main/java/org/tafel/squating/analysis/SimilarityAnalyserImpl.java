package org.tafel.squating.analysis;

import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.springframework.stereotype.Component;
import org.tafel.squating.ports.outbound.SimilarityAnalyzer;

@Component
public class SimilarityAnalyserImpl implements SimilarityAnalyzer {

    private final ColognePhonetic colognePhonetic = new ColognePhonetic();
    private final DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

    @Override
    public SimilarityResult calculateSimilarity(String originalDomain, String candidateDomain) {
        String baseOrig = extractSld(originalDomain);
        String baseCand = extractSld(candidateDomain);

        String colOrig = safeEncodeCologne(baseOrig);
        String colCand = safeEncodeCologne(baseCand);

        String dmOrig = safeEncodeDm(baseOrig);
        String dmCand = safeEncodeDm(baseCand);

        int lev = computeLevenshtein(baseOrig, baseCand);
        int maxLen = Math.max(1, Math.max(baseOrig.length(), baseCand.length()));
        double visualSim = Math.max(0.0, 1.0 - ((double) lev / maxLen));

        boolean colMatch = !colOrig.isEmpty() && colOrig.equals(colCand);
        boolean dmMatch = !dmOrig.isEmpty() && dmOrig.equals(dmCand);

        int colDist = computeLevenshtein(colOrig, colCand);
        int maxColLen = Math.max(1, Math.max(colOrig.length(), colCand.length()));
        double colSim = 1.0 - ((double) colDist / maxColLen);

        double phoneticSim = (colMatch ? 1.0 : (dmMatch ? 0.9 : Math.max(0.0, colSim)));

        return new SimilarityResult(
            Math.round(visualSim * 100.0) / 100.0,
            Math.round(phoneticSim * 100.0) / 100.0,
            lev,
            colOrig,
            colCand,
            dmOrig,
            dmCand,
            colMatch
        );
    }

    private String safeEncodeCologne(String s) {
        try {
            return colognePhonetic.colognePhonetic(s);
        } catch (Exception e) {
            return "";
        }
    }

    private String safeEncodeDm(String s) {
        try {
            return doubleMetaphone.doubleMetaphone(s);
        } catch (Exception e) {
            return "";
        }
    }

    private String extractSld(String domain) {
        if (domain == null) return "";
        String clean = domain.toLowerCase().trim();
        int dot = clean.indexOf('.');
        return dot > 0 ? clean.substring(0, dot) : clean;
    }

    private int computeLevenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
