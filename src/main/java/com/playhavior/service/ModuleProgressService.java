package com.playhavior.service;

import com.playhavior.content.ChatRound;
import com.playhavior.content.ModuleContentCatalog;
import com.playhavior.content.Pledge;
import com.playhavior.content.RiskScenario;
import com.playhavior.content.SandboxSegment;
import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.PathwayModule;
import com.playhavior.entity.ReintegrationContract;
import com.playhavior.model.ModuleStatus;
import com.playhavior.model.ReintegrationTrack;
import com.playhavior.repository.PathwayModuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Runs a player through the interactive modules (service layer):
 * opens a module, grades a submission, records telemetry, unlocks the next module.
 *
 * CALLED BY: ModuleController (open + complete).
 * CALLS: PathwayModuleRepository, ModuleContentCatalog (the correct answers),
 *        ReflectionSentimentAnalyzer (contract reflection).
 * RULES:
 *   - Modules are done in order: only the AVAILABLE module can be opened or completed.
 *   - Every graded decision must END on the correct / restorative choice; wrong
 *     choices are allowed along the way but are counted as extra attempts.
 * TELEMETRY recorded per module (shown in the appeal packet):
 *   questionCount, firstPassCorrect, totalAttempts -> First-Pass Accuracy
 *   startedAt, completedAt, secondsSpent           -> Reading/Engagement Velocity
 *   standingPoints                                 -> Accountability Sandbox score
 *   contract sentiment                             -> Sentiment Alignment
 */
@Service
public class ModuleProgressService {

    private final PathwayModuleRepository moduleRepository;
    private final ModuleContentCatalog catalog;
    private final ReflectionSentimentAnalyzer sentimentAnalyzer;

    public ModuleProgressService(
            PathwayModuleRepository moduleRepository,
            ModuleContentCatalog catalog,
            ReflectionSentimentAnalyzer sentimentAnalyzer
    ) {
        this.moduleRepository = moduleRepository;
        this.catalog = catalog;
        this.sentimentAnalyzer = sentimentAnalyzer;
    }

    // ===== OPEN A MODULE =====

    /**
     * Loads a module for its page and starts the clock the first time it is opened.
     * LOCKED -> 409 (finish the previous module first). COMPLETED -> opens in review mode.
     * WHY no save() call: inside @Transactional, Hibernate writes changed fields of
     *     loaded entities automatically when the method ends ("dirty checking").
     */
    @Transactional
    public PathwayModule openModule(Long pathwayId, Long moduleId) {
        PathwayModule module = findModule(pathwayId, moduleId);

        if (module.getStatus() == ModuleStatus.LOCKED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Finish the previous module to unlock this one.");
        }

        if (module.getStatus() == ModuleStatus.AVAILABLE && module.getStartedAt() == null) {
            module.setStartedAt(LocalDateTime.now());
        }

        return module;
    }

    // ===== COMPLETE A MODULE =====

    /**
     * Grades a submission. If it passes: records the telemetry, marks the module
     * COMPLETED and unlocks the next one. If not: throws InvalidSubmissionException
     * and nothing is saved.
     */
    @Transactional
    public LearningPathway completeModule(Long pathwayId, Long moduleId, ModuleSubmission submission) {
        PathwayModule module = findModule(pathwayId, moduleId);

        // Guard: stops skipping ahead by posting to a locked module's URL
        if (module.getStatus() != ModuleStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This module is not the one currently available.");
        }

        LearningPathway pathway = module.getPathway();
        ReintegrationTrack track = pathway.getTrack();

        // 1. Grade the submission for this module's type
        Grade grade = switch (module.getModuleType()) {
            case VALIDATION_HUB -> gradeValidationHub(submission);
            case PERSPECTIVE_SHIFT -> gradePerspectiveShift(submission);
            case RISK_MATRIX -> gradeRiskMatrix(submission);
            case ACCOUNTABILITY_SANDBOX -> gradeSandbox(submission, track);
            case PROBATIONARY_CONTRACT -> signContract(submission, pathway, track);
        };

        // 2. Record the telemetry
        LocalDateTime now = LocalDateTime.now();
        module.setQuestionCount(grade.questions());
        module.setFirstPassCorrect(grade.firstPassCorrect());
        module.setTotalAttempts(grade.totalAttempts());
        module.setStandingPoints(grade.standingPoints());
        module.setCompletedAt(now);
        module.setSecondsSpent(module.getStartedAt() == null
                ? 0
                : Duration.between(module.getStartedAt(), now).getSeconds());

        // 3. Complete it and unlock the next module (order + 1), if there is one
        module.setStatus(ModuleStatus.COMPLETED);
        pathway.getModules().stream()
                .filter(next -> next.getModuleOrder() == module.getModuleOrder() + 1)
                .findFirst()
                .ifPresent(next -> next.setStatus(ModuleStatus.AVAILABLE));

        return pathway;
    }

    // ===== GRADING, ONE METHOD PER MODULE TYPE =====

    // Phase 1: the player must have clicked the flagged-behaviour card
    private Grade gradeValidationHub(ModuleSubmission submission) {
        return gradeQuestions(
                submission,
                List.of(ModuleContentCatalog.HUB_QUESTION_KEY),
                key -> ModuleContentCatalog.FLAGGED_BEHAVIOR_KEY.equals(submission.answers().get(key)),
                false
        );
    }

    // Phase 2A: each chat round must end on a constructive response
    private Grade gradePerspectiveShift(ModuleSubmission submission) {
        List<ChatRound> rounds = catalog.chatRounds();
        return gradeQuestions(
                submission,
                rounds.stream().map(ChatRound::key).toList(),
                key -> {
                    String answer = submission.answers().get(key);
                    return rounds.stream()
                            .filter(round -> round.key().equals(key))
                            .flatMap(round -> round.options().stream())
                            .anyMatch(option -> option.key().equals(answer) && option.constructive());
                },
                false
        );
    }

    // Phase 2B: each scenario must be in its correct risk column
    private Grade gradeRiskMatrix(ModuleSubmission submission) {
        List<RiskScenario> scenarios = catalog.riskScenarios();
        return gradeQuestions(
                submission,
                scenarios.stream().map(RiskScenario::key).toList(),
                key -> scenarios.stream()
                        .anyMatch(scenario -> scenario.key().equals(key)
                                && scenario.correctLevel().name().equals(submission.answers().get(key))),
                false
        );
    }

    // Phase 3: each decision point must end on the compliant choice; earns Standing Points
    private Grade gradeSandbox(ModuleSubmission submission, ReintegrationTrack track) {
        List<SandboxSegment> segments = catalog.sandboxSegments(track);
        return gradeQuestions(
                submission,
                segments.stream().map(SandboxSegment::key).toList(),
                key -> {
                    String answer = submission.answers().get(key);
                    return segments.stream()
                            .filter(segment -> segment.key().equals(key))
                            .flatMap(segment -> segment.choices().stream())
                            .anyMatch(choice -> choice.key().equals(answer) && choice.compliant());
                },
                true
        );
    }

    // Phase 4: valid pledges + a real reflection; creates the ReintegrationContract
    private Grade signContract(ModuleSubmission submission, LearningPathway pathway, ReintegrationTrack track) {
        Set<String> allowed = catalog.pledges(track).stream()
                .map(Pledge::key)
                .collect(Collectors.toSet());

        // LinkedHashSet: removes duplicates but keeps the order they were ticked in
        List<String> chosen = new LinkedHashSet<>(submission.pledges()).stream()
                .filter(allowed::contains)
                .toList();

        if (chosen.size() < ModuleContentCatalog.MIN_PLEDGES) {
            throw new InvalidSubmissionException(
                    "Choose at least " + ModuleContentCatalog.MIN_PLEDGES + " guardrails for your return.");
        }

        String reflection = submission.reflection() == null ? "" : submission.reflection().trim();
        if (reflection.length() < ModuleContentCatalog.MIN_REFLECTION_LENGTH) {
            throw new InvalidSubmissionException(
                    "Write at least " + ModuleContentCatalog.MIN_REFLECTION_LENGTH
                            + " characters of reflection, in your own words.");
        }

        ReflectionSentimentAnalyzer.SentimentResult sentiment = sentimentAnalyzer.analyze(reflection);

        ReintegrationContract contract = new ReintegrationContract();
        contract.setPledgeKeys(new ArrayList<>(chosen));
        contract.setReflectionText(reflection);
        contract.setSentimentLabel(sentiment.label());
        contract.setSentimentScore(sentiment.score());
        contract.setAccountableSignals(sentiment.accountableSignals());
        contract.setDefensiveSignals(sentiment.defensiveSignals());
        contract.setSignedAt(LocalDateTime.now());

        // Saved through LearningPathway's cascade when the transaction ends
        pathway.setContract(contract);

        // Not a graded quiz: no questions, no points
        return new Grade(0, 0, 0, 0);
    }

    // ===== SHARED HELPERS =====

    /*
     * Checks that every question ended on a correct answer, then counts:
     * first-try correct answers, total attempts, and (for the sandbox) Standing Points.
     */
    private Grade gradeQuestions(
            ModuleSubmission submission,
            List<String> questionKeys,
            Predicate<String> isCorrect,
            boolean awardStandingPoints
    ) {
        int firstPass = 0;
        int attempts = 0;
        int points = 0;

        for (String key : questionKeys) {
            if (!isCorrect.test(key)) {
                throw new InvalidSubmissionException(
                        "Every decision has to end on the restorative choice before you can continue.");
            }

            int tries = submission.attemptsFor(key);
            attempts += tries;

            if (tries == 1) {
                firstPass++;
                points += ModuleContentCatalog.POINTS_FIRST_TRY;
            } else {
                points += ModuleContentCatalog.POINTS_AFTER_RETRY;
            }
        }

        return new Grade(
                questionKeys.size(),
                firstPass,
                attempts,
                awardStandingPoints ? points : 0
        );
    }

    // The module, but only if it belongs to this pathway; otherwise 404
    private PathwayModule findModule(Long pathwayId, Long moduleId) {
        return moduleRepository
                .findByModuleIdAndPathway_PathwayId(moduleId, pathwayId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Module not found."));
    }

    // The outcome of grading one module
    private record Grade(int questions, int firstPassCorrect, int totalAttempts, int standingPoints) {
    }

}
