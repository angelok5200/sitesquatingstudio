package org.tafel.squating.ports.outbound;

import org.tafel.squating.analysis.ContentAnalysis;
import org.tafel.squating.domain.value.HttpSnapshot;

public interface ContentAnalyser {
    ContentAnalysis analyze(String brandName, HttpSnapshot httpSnapshot, String rawHtml);
}
