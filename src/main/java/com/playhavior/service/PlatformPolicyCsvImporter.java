package com.playhavior.service;

import com.playhavior.entity.Platform;
import com.playhavior.entity.PlatformPolicy;
import com.playhavior.entity.PolicyRule;
import com.playhavior.model.ViolationCategory;
import com.playhavior.repository.PlatformPolicyRepository;
import com.playhavior.repository.PlatformRepository;
import com.playhavior.repository.PolicyRuleRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.annotation.Order;


import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Loads data/platform-policy-rules.csv into the database at startup (service layer).
 *
 * FLOW: step 0 (startup, before any request).
 * WHY @Order(2): ReferenceDataConfiguration (@Order(1)) must create the platforms
 *     first, because every CSV row names a platform.
 * CSV COLUMNS: platform_key, policy_title, source_url, version_label, effective_date,
 *     retrieved_date, violation_category, section_title, section_reference, rule_summary
 * WHY fail fast: an unknown platform or category stops startup, so bad data
 *     never reaches users.
 * WHY the exists-checks: safe to run twice (no duplicate policies or rules).
 * NOTE: the CSV itself cannot hold comments; document it here instead.
 */
@Component
@Order(2)
public class PlatformPolicyCsvImporter
        implements ApplicationRunner {

    private final PlatformRepository platformRepository;
    private final PlatformPolicyRepository policyRepository;
    private final PolicyRuleRepository ruleRepository;

    public PlatformPolicyCsvImporter(
            PlatformRepository platformRepository,
            PlatformPolicyRepository policyRepository,
            PolicyRuleRepository ruleRepository
    ) {
        this.platformRepository = platformRepository;
        this.policyRepository = policyRepository;
        this.ruleRepository = ruleRepository;
    }

    // ApplicationRunner: Spring calls run() once after startup.
    // WHY @Transactional: all rows import together, or none do.
    @Override
    @Transactional
    public void run(
            org.springframework.boot.ApplicationArguments args
    ) throws Exception {
        ClassPathResource resource =
                new ClassPathResource(
                        "data/platform-policy-rules.csv"
                );

        // try-with-resources: the file closes automatically, even on an error.
        // setHeader + setSkipHeaderRecord: use line 1 as column names (record.get("platform_key")).
        try (
                Reader reader = new InputStreamReader(
                        resource.getInputStream(),
                        StandardCharsets.UTF_8
                );

                CSVParser parser =
                        CSVFormat.RFC4180.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .setTrim(true)
                                .get()
                                .parse(reader)
        ) {
            for (CSVRecord record : parser) {
                importRecord(record);
            }
        }
    }

    // One CSV line: find the platform -> find or create its policy ->
    // save the rule unless it is already there.
    private void importRecord(CSVRecord record) {
        String platformKey =
                record.get("platform_key");

        Platform platform = platformRepository
                .findByPlatformKey(platformKey)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unknown platform in CSV: "
                                        + platformKey
                        )
                );

        String title =
                record.get("policy_title");

        String versionLabel =
                nullIfBlank(
                        record.get("version_label")
                );

        PlatformPolicy policy = policyRepository
                .findFirstByPlatformAndTitleAndVersionLabel(
                        platform,
                        title,
                        versionLabel
                )
                // WHY orElseGet: many rows share one policy; only the first row creates it
                .orElseGet(() ->
                        createPolicy(
                                platform,
                                record,
                                versionLabel
                        )
                );

        ViolationCategory category =
                // Throws on a misspelt category name -> startup fails (fail fast)
                ViolationCategory.valueOf(
                        record.get("violation_category")
                );

        String sectionReference =
                nullIfBlank(
                        record.get("section_reference")
                );

        boolean alreadyImported =
                ruleRepository
                        .existsByPlatformPolicyAndViolationCategoryAndSectionReference(
                                policy,
                                category,
                                sectionReference
                        );

        if (alreadyImported) {
            return;
        }

        PolicyRule rule = new PolicyRule();

        rule.setPlatformPolicy(policy);
        rule.setViolationCategory(category);

        rule.setSectionTitle(
                record.get("section_title")
        );

        rule.setSectionReference(sectionReference);

        rule.setRuleSummary(
                record.get("rule_summary")
        );

        ruleRepository.save(rule);
    }

    // ===== SMALL HELPERS =====

    // A new PlatformPolicy from the first CSV row that mentions it
    private PlatformPolicy createPolicy(
            Platform platform,
            CSVRecord record,
            String versionLabel
    ) {
        PlatformPolicy policy =
                new PlatformPolicy();

        policy.setPlatform(platform);

        policy.setTitle(
                record.get("policy_title")
        );

        policy.setSourceUrl(
                record.get("source_url")
        );

        policy.setVersionLabel(versionLabel);

        policy.setEffectiveDate(
                parseDate(
                        record.get("effective_date")
                )
        );

        LocalDate retrievedDate =
                parseRequiredDate(
                        record.get("retrieved_date")
                );

        policy.setRetrievedAt(
                retrievedDate.atStartOfDay()
        );

        policy.setActive(true);

        return policyRepository.save(policy);
    }

    // Blank date -> null
    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value.trim());
    }

    // Blank date -> today (the retrieved date must always have a value)
    private LocalDate parseRequiredDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.now();
        }

        return LocalDate.parse(value.trim());
    }

    private String nullIfBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
