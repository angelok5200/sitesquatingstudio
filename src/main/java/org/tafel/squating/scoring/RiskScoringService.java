package org.tafel.squating.scoring;

import org.springframework.stereotype.Service;
import org.tafel.squating.analysis.ContentAnalysis;
import org.tafel.squating.analysis.SimilarityResult;
import org.tafel.squating.domain.enums.CandidateStatus;
import org.tafel.squating.domain.enums.MutationType;
import org.tafel.squating.domain.enums.RiskLevel;
import org.tafel.squating.domain.model.CandidateDomain;
import org.tafel.squating.domain.model.DomainObservation;
import org.tafel.squating.domain.model.RiskAssessment;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskScoringService {

    public RiskAssessment evaluateRisk(
        CandidateDomain candidate,
        DomainObservation observation,
        SimilarityResult similarity,
        ContentAnalysis contentAnalysis
    ) {
        int score = 0;
        List<String> triggeredRules = new ArrayList<>();
        String primaryVector = "Typosquatting permutation";

        // 1. Phonetic German Evaluation
        if (similarity != null) {
            if (similarity.isPhoneticallyIdenticalInGerman()) {
                score += RiskRules.WEIGHT_PHONETIC_COLOGNE_IDENTICAL;
                triggeredRules.add("Kölner Phonetik match: candidate sounds identical in spoken German (" + similarity.cologneCodeCandidate() + ")");
                primaryVector = "Phonetic audio-spoofing";
            } else if (similarity.phoneticSimilarity() >= 0.85) {
                score += 15;
                triggeredRules.add("High phonetic similarity (" + (int)(similarity.phoneticSimilarity() * 100) + "%)");
            }

            if (similarity.visualSimilarity() >= 0.85) {
                score += RiskRules.WEIGHT_HIGH_VISUAL_SIMILARITY;
                triggeredRules.add("High visual string similarity (" + (int)(similarity.visualSimilarity() * 100) + "%)");
            }
        }

        // 2. Mutation type specific weight
        if (candidate.getMutationType() == MutationType.QWERTZ) {
            score += RiskRules.WEIGHT_QWERTZ_TYPO;
            triggeredRules.add("High likelihood German QWERTZ keyboard adjacent slip");
        } else if (candidate.getMutationType() == MutationType.COMBOSQUATTING) {
            score += RiskRules.WEIGHT_COMBOSQUAT_LOGIN;
            triggeredRules.add("Combosquatting with high-urgency operational keyword");
        }

        // 3. Technical DNS & Infrastructure Observations
        if (observation != null) {
            if (observation.getMail().hasMx()) {
                score += RiskRules.WEIGHT_ACTIVE_MX;
                triggeredRules.add("ACTIVE MX RECORDS DETECTED: Domain can receive & send spoofed email (" + observation.getMail().mxHosts() + ")");
                primaryVector = "CEO Fraud & Phishing Email Ingress/Egress";
            }

            if (observation.getDns().hasDns()) {
                score += RiskRules.WEIGHT_ACTIVE_WEB;
                triggeredRules.add("Active DNS resolution pointing to live IPs (" + observation.getDns().aRecords() + ")");
            }

            if (observation.getHttp().isLive()) {
                score += 10;
                triggeredRules.add("Active HTTP/HTTPS web server responding with status " + observation.getHttp().statusCode());
            }
        }

        // 4. Content Inspection
        if (contentAnalysis != null) {
            if (contentAnalysis.isPhishingSuspicious()) {
                score += RiskRules.WEIGHT_LOGIN_FORM;
                triggeredRules.add("MALICIOUS CONTENT SIGNATURE: Form containing password/credential fields found");
                primaryVector = "Credential Harvester & Fake Login Portal";
            } else if (contentAnalysis.isParkedPage()) {
                score = Math.min(score, 30); // Cap risk if page is just parked
                triggeredRules.add("Domain parking advertisement detected");
            }
        }

        // Cap score at 100
        score = Math.min(100, Math.max(0, score));

        // Determine Level
        RiskLevel level;
        if (score >= 80) {
            level = RiskLevel.CRITICAL;
        } else if (score >= 60) {
            level = RiskLevel.HIGH;
        } else if (score >= 35) {
            level = RiskLevel.MEDIUM;
        } else if (score >= 15) {
            level = RiskLevel.LOW;
        } else {
            level = RiskLevel.SAFE;
        }

        boolean requiresTakedown = level == RiskLevel.CRITICAL || (level == RiskLevel.HIGH && observation != null && observation.getMail().hasMx());

        // Update candidate object
        candidate.setRiskScore(score);
        candidate.setRiskLevel(level);
        if (requiresTakedown) {
            candidate.setStatus(CandidateStatus.SUSPECTED_PHISHING);
        } else if (observation != null && observation.getMail().hasMx()) {
            candidate.setStatus(CandidateStatus.ACTIVE_MAIL);
        } else if (observation != null && observation.getDns().hasDns()) {
            candidate.setStatus(CandidateStatus.ACTIVE_WEB);
        }

        return new RiskAssessment(
            null,
            candidate.getId(),
            score,
            level,
            triggeredRules,
            primaryVector,
            requiresTakedown
        );
    }
}
