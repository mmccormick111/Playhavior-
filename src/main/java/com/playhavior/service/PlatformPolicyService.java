package com.playhavior.service;

import com.playhavior.entity.PlatformPolicy;
import com.playhavior.entity.PolicyRule;
import com.playhavior.model.ViolationCategory;
import com.playhavior.repository.PlatformPolicyRepository;
import com.playhavior.repository.PolicyRuleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlatformPolicyService {

    private final PlatformPolicyRepository platformPolicyRepository;
    private final PolicyRuleRepository policyRuleRepository;

    public PlatformPolicyService(
            PlatformPolicyRepository platformPolicyRepository,
            PolicyRuleRepository policyRuleRepository
    ) {
        this.platformPolicyRepository =
                platformPolicyRepository;

        this.policyRuleRepository =
                policyRuleRepository;
    }

    public PlatformPolicy findActivePolicy(String platformKey) {
        return platformPolicyRepository
                .findFirstByPlatform_PlatformKeyAndActiveTrueOrderByEffectiveDateDesc(
                        platformKey
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No active policy found for platform: "
                                        + platformKey
                        )
                );
    }

    public List<PolicyRule> findRulesFor(
            String platformKey,
            ViolationCategory category
    ) {
        PlatformPolicy policy = findActivePolicy(platformKey);

        return policyRuleRepository
                .findByPlatformPolicyAndViolationCategoryOrderBySectionTitleAsc(
                        policy,
                        category
                );
    }
    public List<PolicyRule> findRulesFor(
            PlatformPolicy policy,
            ViolationCategory category
    ) {
        return policyRuleRepository
                .findByPlatformPolicyAndViolationCategoryOrderBySectionTitleAsc(
                        policy,
                        category
                );
    }
}
