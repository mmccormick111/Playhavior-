package com.playhavior;

import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.PathwayModule;
import com.playhavior.model.ModuleStatus;
import com.playhavior.model.ModuleType;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.ReintegrationTrack;
import com.playhavior.model.SentimentLabel;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.service.ReflectionSentimentAnalyzer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the four-phase learning modules, the appeal packet and the dashboard.
 * MockMvc drives the real controllers against H2; @Transactional rolls each test back.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LearningModulesAndDashboardTests {

    private static final String ACCOUNTABLE_REFLECTION =
            "I was wrong to insult my teammate. I understand it hurt him and affected the whole "
                    + "team, and I take responsibility. Next time I will mute and report instead.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LearningPathwayRepository pathwayRepository;

    @Autowired
    private ReflectionSentimentAnalyzer sentimentAnalyzer;

    // ===== TRACK ROUTING =====

    // Social violation + permanent ban -> social track, 4 modules incl. the contract
    @Test
    void socialViolationGetsPerspectiveShiftTrack() throws Exception {
        LearningPathway pathway = submitNotice("PERSONAL_INSULTS", "PERMANENT_BAN", null, null);

        assertThat(pathway.getTrack()).isEqualTo(ReintegrationTrack.SOCIAL_BEHAVIORAL);
        assertThat(pathway.getModules()).extracting(PathwayModule::getModuleType).containsExactly(
                ModuleType.VALIDATION_HUB, ModuleType.PERSPECTIVE_SHIFT,
                ModuleType.ACCOUNTABILITY_SANDBOX, ModuleType.PROBATIONARY_CONTRACT);
    }

    // Systemic violation + short ban -> integrity track, educational (no contract)
    @Test
    void systemicViolationGetsRiskMatrixTrack() throws Exception {
        LearningPathway pathway = submitNotice("BOTTING_SCRIPTING", "TEMPORARY_BAN", "3", "DAYS");

        assertThat(pathway.getTrack()).isEqualTo(ReintegrationTrack.SYSTEM_INTEGRITY);
        assertThat(pathway.getPathwayMode()).isEqualTo(PathwayMode.EDUCATIONAL_ONLY);
        assertThat(pathway.getModules()).extracting(PathwayModule::getModuleType).containsExactly(
                ModuleType.VALIDATION_HUB, ModuleType.RISK_MATRIX, ModuleType.ACCOUNTABILITY_SANDBOX);
    }

    // ===== MODULE RULES =====

    @Test
    void lockedModuleCannotBeOpened() throws Exception {
        LearningPathway pathway = submitNotice("PERSONAL_INSULTS", "PERMANENT_BAN", null, null);

        mockMvc.perform(get(moduleUrl(pathway, 1)))
                .andExpect(status().isConflict());
    }

    // A wrong final answer is rejected: back to the module with a message, nothing completed
    @Test
    void wrongAnswerIsRejected() throws Exception {
        LearningPathway pathway = submitNotice("PERSONAL_INSULTS", "PERMANENT_BAN", null, null);

        mockMvc.perform(post(moduleUrl(pathway, 0) + "/complete")
                        .param("answer_hub", "platform"))
                .andExpect(redirectedUrl(moduleUrl(pathway, 0)))
                .andExpect(flash().attributeExists("error"));

        assertThat(module(pathway, 0).getStatus()).isEqualTo(ModuleStatus.AVAILABLE);
    }

    @Test
    void shortReflectionIsRejected() throws Exception {
        LearningPathway pathway = submitNotice("PERSONAL_INSULTS", "PERMANENT_BAN", null, null);
        completeFirstThreeSocialModules(pathway);

        mockMvc.perform(post(moduleUrl(pathway, 3) + "/complete")
                        .param("pledges", "mute_enemy_chat", "loss_break")
                        .param("reflection", "my bad"))
                .andExpect(flash().attributeExists("error"));

        assertThat(pathway.getContract()).isNull();
    }

    // ===== FULL PLAYTHROUGH + APPEAL PACKET =====

    // Every module completed in order -> telemetry recorded -> packet ready for review
    @Test
    void fullSocialPathwayProducesAppealPacket() throws Exception {
        LearningPathway pathway = submitNotice("PERSONAL_INSULTS", "PERMANENT_BAN", null, null);

        // The module page renders and starts the clock
        mockMvc.perform(get(moduleUrl(pathway, 0)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Your responses will be reviewed")));
        assertThat(module(pathway, 0).getStartedAt()).isNotNull();

        completeFirstThreeSocialModules(pathway);

        mockMvc.perform(post(moduleUrl(pathway, 3) + "/complete")
                        .param("pledges", "mute_enemy_chat", "loss_break")
                        .param("reflection", ACCOUNTABLE_REFLECTION))
                .andExpect(redirectedUrl("/pathways/" + pathway.getPathwayId() + "/report"));

        // Telemetry: hub took 2 tries, so it was not right first time
        assertThat(pathway.isFinished()).isTrue();
        assertThat(module(pathway, 0).getFirstPassCorrect()).isZero();
        assertThat(module(pathway, 0).getTotalAttempts()).isEqualTo(2);
        // Sandbox: 2 first-try (100 each) + 1 after a restart (50)
        assertThat(pathway.getStandingPoints()).isEqualTo(250);
        assertThat(pathway.getContract().getSentimentLabel()).isEqualTo(SentimentLabel.ACCOUNTABLE);

        // Accuracy = first-try correct / graded decisions = (0 + 3 + 2) / (1 + 3 + 3) = 71%
        mockMvc.perform(get("/pathways/" + pathway.getPathwayId() + "/report.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readyForReview").value(true))
                .andExpect(jsonPath("$.platformReferenceCode").value("XB-777"))
                .andExpect(jsonPath("$.firstPassAccuracyPercent").value(71))
                .andExpect(jsonPath("$.standingPoints").value(250))
                .andExpect(jsonPath("$.sentimentLabel").value("Accountable language"))
                .andExpect(jsonPath("$.pledges[0]").value("Automatically mute enemy text chat for 14 days"));

        mockMvc.perform(get("/pathways/" + pathway.getPathwayId() + "/report"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ready for review")))
                .andExpect(content().string(containsString("XB-777")));
    }

    // ===== DASHBOARD =====

    @Test
    void dashboardShowsEmptyStateWithoutACase() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Jordan Lee")))
                .andExpect(content().string(containsString("Submit your notice")));
    }

    @Test
    void dashboardShowsProgressAndNextModule() throws Exception {
        LearningPathway pathway = submitNotice("PERSONAL_INSULTS", "PERMANENT_BAN", null, null);
        complete(pathway, 0, "answer_hub", "flagged");

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome back")))
                .andExpect(content().string(containsString("1 of 4 modules done")))
                .andExpect(content().string(containsString("Module 2")))
                .andExpect(content().string(containsString("Perspective Shift")))
                .andExpect(content().string(containsString("1 day")));
    }

    // ===== SENTIMENT ANALYZER =====

    @Test
    void sentimentAnalyzerSeparatesAccountableFromDefensive() {
        assertThat(sentimentAnalyzer.analyze(ACCOUNTABLE_REFLECTION).label())
                .isEqualTo(SentimentLabel.ACCOUNTABLE);
        assertThat(sentimentAnalyzer.analyze(
                        "This ban is unfair, it was just a joke and everyone does it.").label())
                .isEqualTo(SentimentLabel.DEFENSIVE);
    }

    // ===== HELPERS =====

    // Submits a notice through the real form and returns the generated pathway
    private LearningPathway submitNotice(String reason, String penalty, String amount, String unit)
            throws Exception {
        MockHttpServletRequestBuilder request = post("/notices")
                .param("platformKey", "XBOX")
                .param("violationReasonKey", reason)
                .param("penaltyType", penalty)
                .param("banIssueDate", "2026-09-01")
                .param("platformProvidedEvidence", "false")
                .param("platformCaseNumber", "XB-777")
                .param("accuracyAcknowledged", "true")
                .param("limitationAcknowledged", "true");
        if (amount != null) {
            request.param("penaltyDurationAmount", amount).param("penaltyDurationUnit", unit);
        }
        mockMvc.perform(request).andExpect(status().is3xxRedirection());
        return pathwayRepository.findAll().getLast();
    }

    // Hub (2 tries), Perspective Shift (all first try), Sandbox (seg2 needed a restart)
    private void completeFirstThreeSocialModules(LearningPathway pathway) throws Exception {
        complete(pathway, 0, "answer_hub", "flagged", "attempts_hub", "2");
        complete(pathway, 1,
                "answer_round1", "r1c", "answer_round2", "r2d", "answer_round3", "r3c");
        complete(pathway, 2,
                "answer_seg1", "s1b", "answer_seg2", "s2b", "attempts_seg2", "2", "answer_seg3", "s3b");
    }

    // Posts name/value pairs to a module's /complete URL
    private void complete(LearningPathway pathway, int index, String... pairs) throws Exception {
        mockMvc.perform(get(moduleUrl(pathway, index)));   // opens it, starting the clock
        MockHttpServletRequestBuilder request = post(moduleUrl(pathway, index) + "/complete");
        for (int i = 0; i < pairs.length; i += 2) {
            request.param(pairs[i], pairs[i + 1]);
        }
        mockMvc.perform(request).andExpect(flash().attributeCount(1));
        assertThat(module(pathway, index).getStatus()).isEqualTo(ModuleStatus.COMPLETED);
    }

    private PathwayModule module(LearningPathway pathway, int index) {
        return pathway.getModules().get(index);
    }

    private String moduleUrl(LearningPathway pathway, int index) {
        return "/pathways/" + pathway.getPathwayId() + "/modules/" + module(pathway, index).getModuleId();
    }
}
