package org.tafel.squating.analysis;

public record SimilarityResult(
    double visualSimilarity,
    double phoneticSimilarity,
    int levenshteinDistance,
    String cologneCodeOriginal,
    String cologneCodeCandidate,
    String doubleMetaphoneOriginal,
    String doubleMetaphoneCandidate,
    boolean isPhoneticallyIdenticalInGerman
) {
}
