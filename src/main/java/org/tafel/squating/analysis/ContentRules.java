package org.tafel.squating.analysis;

import java.util.List;
import java.util.regex.Pattern;

public class ContentRules {
    public static final Pattern LOGIN_FORM_PATTERN = Pattern.compile("(?i)<input[^>]+type=[\"']password[\"']|form.*action=.*login");
    public static final Pattern PARKED_PAGE_PATTERN = Pattern.compile("(?i)(buy this domain|domain is parked|sedo parking|godaddy parking|domain zum verkauf)");
    public static final Pattern PHISHING_KIT_PATTERN = Pattern.compile("(?i)(verify your account|konto sperrung|tan-eingabe|sicherheits-update|passwort zur[uü]cksetzen)");

    public static final List<String> GERMAN_SUSPICIOUS_KEYWORDS = List.of(
        "login", "banking", "online-banking", "sicherheit", "legitimation", "ausweis", "konto", "sparkasse", "portal"
    );
}
