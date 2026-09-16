package org.tafel.squating.ports.outbound;

import org.tafel.squating.analysis.SimilarityResult;

public interface SimilarityAnalyzer {
    SimilarityResult calculateSimilarity(String originalDomain, String candidateDomain);
}
