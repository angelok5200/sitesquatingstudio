# German Sitesquatting Monitor & Risk Assessor (Java 17 / Spring Boot 3)

A production-grade, single-site typosquatting and sitesquatting detector tailored specifically for German organizations, banking/e-commerce brands, and the DACH region (`.de`, `.at`, `.ch`, `.berlin`).

## Key Features

1. **German Phonetic Analysis (Kölner Phonetik & Double Metaphone)**
   - Implements Hans Joachim Postel's *Kölner Phonetik* (Cologne Phonetics) algorithm via `org.apache.commons.codec.language.ColognePhonetic`.
   - Groups phonetic equivalents common in German speech (`v`/`f`, `ph`/`f`, `ei`/`ai`/`ey`, `ck`/`k`, `dt`/`tt`).
   - Implements *Double Metaphone* and *Soundex* with Levenshtein lexical distance.

2. **German Typosquatting Generators**
   - **Homoglyphs & Optical Swaps:** `m` ↔ `rn`, `w` ↔ `vv`, `d` ↔ `cl`, `l` ↔ `1`/`i`.
   - **QWERTZ German Keyboard Typos:** Physical key neighbors and the German `z` ↔ `y` position swap.
   - **German Umlauts & Transliterations:** `ä` ↔ `ae`, `ö` ↔ `oe`, `ü` ↔ `ue`.
   - **German Eszett:** `ß` ↔ `ss`, `sz`.
   - **German Combosquatting:** German banking and phishing vectors (`-login`, `-konto`, `-sicherheit`, `-banking`, `-portal`).
   - **DACH TLDs:** `.de`, `.at`, `.ch`, `.berlin`, `.hamburg`, `.bayern`, `.eu`, `.com`.

3. **Security Risk Assessment Engine (0–100 Scale)**
   - **Critical Threat (MX Records):** Detects active Mail Exchange records (+35 pts) indicating active email interception, credential phishing, or CEO-fraud risk.
   - **Active Web Host (A/AAAA Records):** Resolves IP addresses (+20 pts).
   - **Cologne Phonetic Exact Match:** (+20 pts) for acoustic spoofing.
   - **German Phishing Keywords:** (+25 pts) for `login`, `konto`, `sicherheit`.

4. **Weekly Automated Scheduler**
   - Implements Spring Boot `@Scheduled(cron = "0 0 2 * * MON")` to audit for new squatters every Monday at 02:00 UTC.
   - Tracks new registrations with an `isNew` delta flag.

5. **Single-Site Focus**
   - Tailored specifically to protect a single monitored enterprise domain (e.g. `sparda-bank.de`, `zalando.de`, etc.).

---

## Build & Run Instructions

### Prerequisites
- JDK 17 or higher
- Maven 3.8+

### Compile & Run Tests
```bash
mvn clean test
```

### Package Executable JAR
```bash
mvn clean package -DskipTests
```

### Run the Application
```bash
# Start as Spring Boot microservice on port 8080:
java -jar target/german-sitesquatting-monitor-1.0.0.jar

# Or pass a custom target domain via CLI argument:
java -jar target/german-sitesquatting-monitor-1.0.0.jar --domain=zalando.de
```

### REST API Endpoints
- `GET  /api/health` - Service health status
- `GET  /api/config` - Get target site configuration
- `POST /api/config` - Update target domain or settings
- `GET  /api/domains` - List all generated permutations, DNS records, and risk scores
- `POST /api/scan` - Trigger an immediate full DNS audit
- `GET  /api/scans` - Audit log of weekly scheduled scans
- `GET  /api/phonetics/compare?target=sparda&candidate=sparcla` - Compare phonetic codes
- `GET  /api/export/csv` - Download CSV report
