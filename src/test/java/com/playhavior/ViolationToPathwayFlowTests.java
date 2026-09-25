package com.playhavior;

import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.PathwayModule;
import com.playhavior.model.ModuleStatus;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.ViolationCategory;
import com.playhavior.repository.BanReportRepository;
import com.playhavior.repository.LearningPathwayRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ViolationToPathwayFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LearningPathwayRepository pathwayRepository;

    @Autowired
    private BanReportRepository banReportRepository;

    @Test
    void homeRedirectsToNoticeForm() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/notices/new"));
    }

    @Test
    void noticeFormRendersWithPlatformsAndStylesheet() throws Exception {
        mockMvc.perform(get("/notices/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/css/playhavior.css\"")))
                .andExpect(content().string(containsString("PlayStation Network")));
    }

    @Test
    void submittingNoticeGeneratesAndStoresPathway() throws Exception {
        MvcResult result = mockMvc.perform(post("/notices")
                        .param("platformKey", "XBOX")
                        .param("gameTitle", "Halo Infinite")
                        .param("violationReasonKey", "PERSONAL_INSULTS")
                        .param("penaltyType", "PERMANENT_BAN")
                        .param("banIssueDate", "2026-09-01")
                        .param("platformProvidedEvidence", "false")
                        .param("platformCaseNumber", "XB-12345")
                        .param("accuracyAcknowledged", "true")
                        .param("limitationAcknowledged", "true"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        LearningPathway pathway = pathwayRepository.findAll().getLast();

        assertThat(result.getResponse().getRedirectedUrl())
                .isEqualTo("/pathways/" + pathway.getPathwayId());
        assertThat(pathway.getViolationCategory())
                .isEqualTo(ViolationCategory.HARASSMENT_BULLYING);
        assertThat(pathway.getPathwayMode())
                .isEqualTo(PathwayMode.REINSTATEMENT_SUPPORT);
        assertThat(pathway.getTotalModules()).isEqualTo(5);
        assertThat(pathway.getModules())
                .extracting(PathwayModule::getStatus)
                .containsExactly(
                        ModuleStatus.AVAILABLE,
                        ModuleStatus.LOCKED,
                        ModuleStatus.LOCKED,
                        ModuleStatus.LOCKED,
                        ModuleStatus.LOCKED
                );
        assertThat(banReportRepository.findAll().getLast().getPlatformCaseNumber())
                .isEqualTo("XB-12345");

        mockMvc.perform(get("/pathways/" + pathway.getPathwayId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Respectful Communication")))
                .andExpect(content().string(containsString("Module 1: Understanding Xbox Community Standards")))
                .andExpect(content().string(containsString("XB-12345")))
                .andExpect(content().string(containsString("Standard 2")));
    }

    @Test
    void invalidSubmissionReturnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/notices")
                        .param("penaltyType", "TEMPORARY_BAN"))
                .andExpect(status().isOk())
                .andExpect(view().name("violation-intake"))
                .andExpect(model().attributeHasFieldErrors(
                        "violationForm",
                        "platformKey",
                        "penaltyDurationAmount",
                        "accuracyAcknowledged"))
                .andExpect(content().string(containsString("Please correct the highlighted fields.")));
    }

    @Test
    void unknownPathwayReturns404() throws Exception {
        mockMvc.perform(get("/pathways/999999"))
                .andExpect(status().isNotFound());
    }
}
