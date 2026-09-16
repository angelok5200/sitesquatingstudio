package org.tafel.squating.application;

import org.springframework.stereotype.Service;
import org.tafel.squating.analysis.SimilarityResult;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.generators.CandidateGenerator;
import org.tafel.squating.ports.outbound.CandidateRepository;
import org.tafel.squating.ports.outbound.SimilarityAnalyzer;

import java.util.*;

@Service
public class CandidateGenerationService {

    private final List<CandidateGenerator> generators;
    private final CandidateRepository candidateRepository;
    private final SimilarityAnalyzer similarityAnalyzer;

    public CandidateGenerationService(
        List<CandidateGenerator> generators,
        CandidateRepository candidateRepository,
        SimilarityAnalyzer similarityAnalyzer
    ) {
        this.generators = generators;
        this.candidateRepository = candidateRepository;
        this.similarityAnalyzer = similarityAnalyzer;
    }

    public List<CandidateDomain> generateCandidatesForBrand(Brand brand) {
        Map<String, CandidateDomain> uniqueCandidates = new LinkedHashMap<>();

        for (CandidateGenerator generator : generators) {
            if (brand.getPolicy().enabledMutations().contains(generator.getMutationType())) {
                List<CandidateDomain> generated = generator.generate(brand);
                for (CandidateDomain candidate : generated) {
                    if (!candidate.getDomainName().equalsIgnoreCase(brand.getPrimaryDomain())) {
                        uniqueCandidates.putIfAbsent(candidate.getDomainName(), candidate);
                    }
                }
            }
        }

        // Enrich with Kölner Phonetik & Double Metaphone similarity
        List<CandidateDomain> resultList = new ArrayList<>(uniqueCandidates.values());
        for (CandidateDomain candidate : resultList) {
            SimilarityResult sim = similarityAnalyzer.calculateSimilarity(brand.getPrimaryDomain(), candidate.getDomainName());
            candidate.setVisualSimilarity(sim.visualSimilarity());
            candidate.setPhoneticSimilarity(sim.phoneticSimilarity());
            candidate.setCologneCode(sim.cologneCodeCandidate());
            candidate.setDoubleMetaphoneCode(sim.doubleMetaphoneCandidate());
        }

        candidateRepository.saveAll(resultList);
        return resultList;
    }
}
