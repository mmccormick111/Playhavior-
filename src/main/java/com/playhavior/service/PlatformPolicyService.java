package com.playhavior.service;

import com.playhavior.entity.PlatformPolicy;
import com.playhavior.entity.PolicyRule;
import com.playhavior.model.ViolationCategory;
import com.playhavior.repository.PlatformPolicyRepository;
import com.playhavior.repository.PolicyRuleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Finds a platform's current policy and its rules for a category (service layer).
 *
 * CALLED BY: PlayhaviorWorkflowService (findActivePolicy, FLOW step 4),
 *            LearningPathwayController (findRulesFor, FLOW step 7).
 * NOTE: the two findRulesFor methods are OVERLOADING: same name, different parameters.
 */
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

    // Newest active policy for a platform key, or an exception if none was imported
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

    // Version 1: starts from a platform key and looks the policy up first
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
    // Version 2: starts from a policy (the pathway already stores its policy)
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
