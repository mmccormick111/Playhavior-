package com.playhavior;

import com.playhavior.entity.Player;
import com.playhavior.entity.Case;
import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.SummaryReport;
import com.playhavior.repository.PlayerProfileRepository;
import com.playhavior.repository.CaseRepository;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.repository.SummaryReportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataConfig {

    @Bean
    CommandLineRunner seedDemoData(
            PlayerProfileRepository playerProfileRepository,
            CaseRepository caseRepository,
            LearningPathwayRepository learningPathwayRepository,
            SummaryReportRepository summaryReportRepository) {

        return args -> {

            if (playerProfileRepository.count() == 0) {

                Player player = new Player();
                player.setDisplay_name("XulaNugget");
                player.setEmail("xunugget@example.com");
                playerProfileRepository.save(player);

                Case playerCase = new Case();
                playerCase.setStatus("Open");
                playerCase.setDescription(
                        "Repeated abusive chat messages reported during ranked matches.");
                caseRepository.save(playerCase);

                LearningPathway pathway = new LearningPathway();
                pathway.setPathway_title("Respectful Communication Reset");
                pathway.setTotal_modules(6);
                learningPathwayRepository.save(pathway);

                SummaryReport summary = new SummaryReport();
                summary.setNarrative_text(
                        "Case created for NovaStride. Learning pathway assigned for conduct remediation.");
                summary.setVerification_code(1002);
                summaryReportRepository.save(summary);
            }
        };
    }
}
