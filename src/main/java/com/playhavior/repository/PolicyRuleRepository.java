package com.playhavior.repository;
import com.playhavior.entity.PlatformPolicy;
import com.playhavior.entity.PolicyRule;
import com.playhavior.model.ViolationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRuleRepository
        extends JpaRepository<PolicyRule, Long> {

    List<PolicyRule>
    findByPlatformPolicyAndViolationCategoryOrderBySectionTitleAsc(
            PlatformPolicy platformPolicy,
            ViolationCategory violationCategory
    );
    boolean existsByPlatformPolicyAndViolationCategoryAndSectionReference(
            PlatformPolicy platformPolicy,
            ViolationCategory violationCategory,
            String sectionReference
    );
}
