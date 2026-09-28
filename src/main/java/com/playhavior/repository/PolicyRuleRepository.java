package com.playhavior.repository;
import com.playhavior.entity.PlatformPolicy;
import com.playhavior.entity.PolicyRule;
import com.playhavior.model.ViolationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Database access for PolicyRule. */
public interface PolicyRuleRepository
        extends JpaRepository<PolicyRule, Long> {

    // All rules of one policy for one category, sorted. USED BY: PlatformPolicyService.findRulesFor()
    List<PolicyRule>
    findByPlatformPolicyAndViolationCategoryOrderBySectionTitleAsc(
            PlatformPolicy platformPolicy,
            ViolationCategory violationCategory
    );
    // Is this rule already imported? USED BY: CSV importer (safe to run twice)
    boolean existsByPlatformPolicyAndViolationCategoryAndSectionReference(
            PlatformPolicy platformPolicy,
            ViolationCategory violationCategory,
            String sectionReference
    );
}
