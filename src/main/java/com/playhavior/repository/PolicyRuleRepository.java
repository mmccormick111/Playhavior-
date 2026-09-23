package com.playhavior.repository;

import com.playhavior.entity.PolicyRule;
import com.playhavior.model.ViolationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRuleRepository
        extends JpaRepository<PolicyRule, Long> {

    List<PolicyRule>
    findByPlatformPolicyPolicyIdAndViolationCategoryOrderBySectionTitleAsc(
            Long policyId,
            ViolationCategory violationCategory
    );
}
