package org.tafel.squating.domain.value;

import org.tafel.squating.domain.enums.MutationType;

import java.util.EnumSet;
import java.util.Set;

/**
 * Value Object defining execution policy for brand squatting surveillance.
 */
public record MonitoringPolicy(
    int scanIntervalHours,
    int maxCandidatesPerRun,
    int alertRiskThreshold,
    boolean checkMxRecords,
    boolean checkHttpContent,
    boolean checkCertificates,
    Set<MutationType> enabledMutations
) {
    public static MonitoringPolicy defaultGermanPolicy() {
        return new MonitoringPolicy(
            168, // Weekly (7 days)
            250,
            60,  // High/Critical risk threshold
            true,
            true,
            true,
            EnumSet.of(
                MutationType.OMISSION,
                MutationType.DUPLICATION,
                MutationType.TRANSPOSITION,
                MutationType.QWERTZ,
                MutationType.SUBSTITUTION,
                MutationType.HOMOGLYPH,
                MutationType.UMLAUT,
                MutationType.COMBOSQUATTING,
                MutationType.PHONETIC_GERMAN,
                MutationType.TLD_SWAP
            )
        );
    }
}
