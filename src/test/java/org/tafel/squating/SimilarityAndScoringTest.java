package org.tafel.squating;

import org.junit.jupiter.api.Test;
import org.tafel.squating.analysis.SimilarityAnalyserImpl;
import org.tafel.squating.analysis.SimilarityResult;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.enums.RiskLevel;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.domain.value.DnsSnapshot;
import org.tafel.squating.domain.value.HttpSnapshot;
import org.tafel.squating.domain.value.MailSnapshot;
import org.tafel.squating.domain.value.RegistrationSnapshot;
import org.tafel.squating.domain.model.DomainObservation;
import org.tafel.squating.domain.model.RiskAssessment;
import org.tafel.squating.generators.QwertzGenerator;
import org.tafel.squating.scoring.RiskScoringService;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimilarityAndScoringTest {

    private final SimilarityAnalyserImpl similarityAnalyser = new SimilarityAnalyserImpl();
    private final RiskScoringService riskScoringService = new RiskScoringService();
    private final QwertzGenerator qwertzGenerator = new QwertzGenerator();

    @Test
    public void testGermanColognePhoneticMatch() {
        // "tafel" and "tafle" share phonetic representation in German
        SimilarityResult result = similarityAnalyser.calculateSimilarity("tafel.de", "tafle.de");
        assertNotNull(result.cologneCodeCandidate());
        assertTrue(result.phoneticSimilarity() > 0.8, "Phonetic similarity should be very high for transposed German vowels");
    }

    @Test
    public void testQwertzKeyboardNeighborGeneration() {
        Brand brand = Brand.of("Tafel", "tafel.de");
        List<CandidateDomain> candidates = qwertzGenerator.generate(brand);

        assertFalse(candidates.isEmpty(), "Candidates should be generated for QWERTZ keyboard");
        boolean hasAdjacent = candidates.stream().anyMatch(c -> c.getMutationType() == MutationType.QWERTZ);
        assertTrue(hasAdjacent);
    }

    @Test
    public void testCriticalRiskOnActiveMxAndCombosquatting() {
        CandidateDomain candidate = new CandidateDomain(
            "test-id",
            "brand-1",
            "sparda-login.de",
            "sparda-bank.de",
            MutationType.COMBOSQUATTING,
            "Combosquatting with login keyword"
        );

        DomainObservation observation = new DomainObservation(
            "obs-1",
            candidate.getId(),
            RegistrationSnapshot.unregistered(),
            new DnsSnapshot(true, List.of("192.0.2.1"), List.of(), List.of("mail.sparda-login.de"), List.of(), List.of(), Instant.now()),
            HttpSnapshot.unreachable(),
            null,
            new MailSnapshot(true, List.of("mail.sparda-login.de"), false, false, true)
        );

        SimilarityResult sim = similarityAnalyser.calculateSimilarity(candidate.getOriginalDomain(), candidate.getDomainName());
        RiskAssessment assessment = riskScoringService.evaluateRisk(candidate, observation, sim, null);

        assertNotNull(assessment);
        assertTrue(assessment.getTotalScore() >= 60, "Score should reflect active MX and combosquatting");
        assertTrue(assessment.getRiskLevel() == RiskLevel.HIGH || assessment.getRiskLevel() == RiskLevel.CRITICAL);
    }
}
