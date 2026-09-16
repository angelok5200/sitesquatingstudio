package org.tafel.squating.application;

import org.springframework.stereotype.Service;
import org.tafel.squating.analysis.ContentAnalysis;
import org.tafel.squating.analysis.SimilarityResult;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.domain.model.DomainObservation;
import org.tafel.squating.domain.model.RiskAssessment;
import org.tafel.squating.ports.outbound.*;
import org.tafel.squating.scoring.RiskScoringService;

@Service
public class RiskAssessmentService {

    private final RiskScoringService riskScoringService;
    private final SimilarityAnalyzer similarityAnalyzer;
    private final ContentAnalyser contentAnalyser;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final CandidateRepository candidateRepository;

    public RiskAssessmentService(
        RiskScoringService riskScoringService,
        SimilarityAnalyzer similarityAnalyzer,
        ContentAnalyser contentAnalyser,
        RiskAssessmentRepository riskAssessmentRepository,
        CandidateRepository candidateRepository
    ) {
        this.riskScoringService = riskScoringService;
        this.similarityAnalyzer = similarityAnalyzer;
        this.contentAnalyser = contentAnalyser;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.candidateRepository = candidateRepository;
    }

    public RiskAssessment assessCandidateRisk(CandidateDomain candidate, DomainObservation observation) {
        SimilarityResult similarity = similarityAnalyzer.calculateSimilarity(
            candidate.getOriginalDomain(),
            candidate.getDomainName()
        );

        ContentAnalysis contentAnalysis = null;
        if (observation != null && observation.getHttp().isLive()) {
            contentAnalysis = contentAnalyser.analyze(
                candidate.getOriginalDomain(),
                observation.getHttp(),
                "<title>" + observation.getHttp().pageTitle() + "</title>"
            );
        }

        RiskAssessment assessment = riskScoringService.evaluateRisk(
            candidate,
            observation,
            similarity,
            contentAnalysis
        );

        riskAssessmentRepository.save(assessment);
        candidateRepository.save(candidate);

        return assessment;
    }
}
