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

    @Override
    @Transactional
    public void run(
            org.springframework.boot.ApplicationArguments args
    ) throws Exception {
        ClassPathResource resource =
                new ClassPathResource(
                        "data/platform-policy-rules.csv"
                );

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
                .orElseGet(() ->
                        createPolicy(
                                platform,
                                record,
                                versionLabel
                        )
                );

        ViolationCategory category =
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

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value.trim());
    }

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
