package org.tafel.squating.scoring;

import java.util.List;

public class RiskRules {
    public static final int WEIGHT_ACTIVE_MX = 35;
    public static final int WEIGHT_ACTIVE_WEB = 15;
    public static final int WEIGHT_LOGIN_FORM = 35;
    public static final int WEIGHT_PHONETIC_COLOGNE_IDENTICAL = 25;
    public static final int WEIGHT_HIGH_VISUAL_SIMILARITY = 20;
    public static final int WEIGHT_QWERTZ_TYPO = 15;
    public static final int WEIGHT_COMBOSQUAT_LOGIN = 25;
    public static final int WEIGHT_RECENT_REGISTRATION = 15;

    public static final List<String> HIGH_RISK_KEYWORDS = List.of(
        "login", "banking", "online-banking", "sicherheit", "portal", "konto", "auth"
    );
}
