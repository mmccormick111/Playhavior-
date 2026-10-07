// Interactive behaviour for the five module pages (module-*.html).
// Each page's <main> has data-module="..." which picks the setup function below.
//
// HOW RESULTS REACH THE SERVER: every graded question has two hidden inputs in the
// page's form: answer_<key> (the final choice) and attempts_<key> (how many tries).
// The server re-checks every final answer (ModuleProgressService), so this file is
// about the experience; it cannot mark a wrong answer as right.
// In review mode (module already completed) there is no form; the pages still work
// as practice, but nothing is submitted.

document.addEventListener("DOMContentLoaded", () => {
    const page = document.querySelector("[data-module]");
    if (!page) {
        return;
    }

    const setups = {
        "validation-hub": setupValidationHub,
        "perspective-shift": setupPerspectiveShift,
        "risk-matrix": setupRiskMatrix,
        "sandbox": setupSandbox,
        "contract": setupContract
    };

    const setup = setups[page.dataset.module];
    if (setup) {
        setup(page);
    }
});

// ===== SHARED HELPERS =====

// Writes a value into a hidden input of the module form (if the form exists)
function setHidden(name, value) {
    const input = document.querySelector(`input[name="${name}"]`);
    if (input) {
        input.value = value;
    }
}

// Shows the completion form at the bottom of the page and scrolls to it
function revealForm(page) {
    const form = page.querySelector(".unlock-panel");
    if (form) {
        form.hidden = false;
        form.scrollIntoView({ behavior: "smooth", block: "center" });
    }
}

function showFeedback(element, text, good) {
    element.textContent = text;
    element.classList.toggle("good", good);
    element.classList.toggle("bad", !good);
}

// ===== PHASE 1: VALIDATION HUB =====
// The player must click the flagged-behaviour card. Clicking a context card
// (platform, penalty, date) is an extra attempt.
function setupValidationHub(page) {
    const hint = page.querySelector("[data-role='hint']");
    let attempts = 0;
    let found = false;

    page.querySelectorAll(".notice-item").forEach(item => {
        item.addEventListener("click", () => {
            if (found) {
                return;
            }
            attempts++;

            if (item.dataset.key === "flagged") {
                found = true;
                item.classList.add("selected");
                showFeedback(hint, "That's the behavior your restriction is for. Owning it unlocks the path.", true);
                setHidden("answer_hub", "flagged");
                setHidden("attempts_hub", attempts);
                revealForm(page);
            } else {
                item.classList.add("dimmed");
                showFeedback(hint, "That's context, not the cause. Find the behavior the platform flagged.", false);
            }
        });
    });
}

// ===== PHASE 2A: PERSPECTIVE SHIFT (social track) =====
// Each response moves the Morale and Performance bars. Only a constructive
// response finishes a round; harmful ones are counted and disabled.
function setupPerspectiveShift(page) {
    const start = Number(page.dataset.start) || 70;
    const bars = { morale: start, performance: start };

    function drawBars() {
        Object.keys(bars).forEach(name => {
            bars[name] = Math.max(0, Math.min(100, bars[name]));
            const fill = page.querySelector(`[data-meter="${name}"]`);
            fill.style.width = bars[name] + "%";
            fill.classList.toggle("low", bars[name] < 40);
            page.querySelector(`[data-meter-value="${name}"]`).textContent = bars[name] + "%";
        });
    }

    drawBars();

    const rounds = Array.from(page.querySelectorAll(".round"));

    rounds.forEach((round, index) => {
        const key = round.dataset.round;
        const select = round.querySelector("[data-role='choice']");
        const send = round.querySelector("[data-role='send']");
        const feedback = round.querySelector("[data-role='feedback']");
        const next = round.querySelector("[data-role='next']");
        let attempts = 0;

        send.addEventListener("click", () => {
            const option = select.selectedOptions[0];
            if (!option || !option.value) {
                showFeedback(feedback, "Pick a response from the menu first.", false);
                return;
            }

            attempts++;
            bars.morale += Number(option.dataset.morale);
            bars.performance += Number(option.dataset.performance);
            drawBars();

            if (option.dataset.constructive === "true") {
                showFeedback(feedback, option.dataset.feedback, true);
                select.disabled = true;
                send.disabled = true;
                setHidden(`answer_${key}`, option.value);
                setHidden(`attempts_${key}`, attempts);

                if (index === rounds.length - 1) {
                    revealForm(page);
                } else {
                    next.hidden = false;
                }
            } else {
                showFeedback(feedback, option.dataset.feedback + " Choose a different response.", false);
                option.disabled = true;
                select.value = "";
            }
        });

        next.addEventListener("click", () => {
            round.hidden = true;
            rounds[index + 1].hidden = false;
            rounds[index + 1].scrollIntoView({ behavior: "smooth", block: "start" });
        });
    });
}

// ===== PHASE 2B: RISK MATRIX (system integrity track) =====
// Narration via the browser's speech engine (Modality Principle), then the player
// sorts each scenario; correct cards move into their column with the reason why.
function setupRiskMatrix(page) {
    const narrate = page.querySelector("[data-role='narrate']");
    const transcript = page.querySelector("[data-role='transcript']");
    const flow = page.querySelector(".impact-flow");

    narrate.addEventListener("click", () => {
        flow.classList.remove("playing");
        void flow.offsetWidth;            // restart the CSS animation
        flow.classList.add("playing");

        if (!("speechSynthesis" in window)) {
            narrate.textContent = "Audio not supported in this browser; read the transcript below";
            return;
        }
        if (window.speechSynthesis.speaking) {
            window.speechSynthesis.cancel();
            narrate.textContent = "▶ Play narration";
            return;
        }
        const speech = new SpeechSynthesisUtterance(transcript.textContent);
        speech.rate = 0.95;
        speech.onend = () => { narrate.textContent = "▶ Play narration"; };
        window.speechSynthesis.speak(speech);
        narrate.textContent = "■ Stop narration";
    });

    const cards = Array.from(page.querySelectorAll(".scenario-card"));
    let sorted = 0;

    cards.forEach(card => {
        const key = card.dataset.scenario;
        const feedback = card.querySelector("[data-role='feedback']");
        let attempts = 0;

        card.querySelectorAll(".level-button").forEach(button => {
            button.addEventListener("click", () => {
                attempts++;

                if (button.dataset.level === card.dataset.correct) {
                    showFeedback(feedback, card.dataset.explanation, true);
                    card.classList.add("sorted");
                    card.querySelectorAll(".level-button").forEach(b => { b.disabled = true; });
                    setHidden(`answer_${key}`, button.dataset.level);
                    setHidden(`attempts_${key}`, attempts);

                    // Move the card into its column of the matrix
                    page.querySelector(`[data-column="${button.dataset.level}"]`).appendChild(card);

                    sorted++;
                    if (sorted === cards.length) {
                        revealForm(page);
                    }
                } else {
                    button.disabled = true;
                    button.classList.add("wrong");
                    showFeedback(feedback,
                        "Not quite. Think about who it harms and whether the platform allows it.", false);
                }
            });
        });
    });
}

// ===== PHASE 3: ACCOUNTABILITY SANDBOX =====
// Each segment: setup -> catalyst -> timed choice. A harmful choice or running out
// of time halts the simulation and restarts the segment; a restorative choice
// advances and earns Standing Points.
function setupSandbox(page) {
    const seconds = Number(page.dataset.seconds) || 10;
    const pointsFirst = Number(page.dataset.pointsFirst) || 100;
    const pointsRetry = Number(page.dataset.pointsRetry) || 50;
    const pointsDisplay = page.querySelector("[data-role='points']");
    const countDisplay = page.querySelector("[data-role='segment-count']");
    const segments = Array.from(page.querySelectorAll(".segment"));
    let points = 0;

    countDisplay.textContent = `1 / ${segments.length}`;

    segments.forEach((segment, index) => {
        const key = segment.dataset.segment;
        const stage = name => segment.querySelector(`[data-stage="${name}"]`);
        const startButton = segment.querySelector("[data-role='start-timer']");
        const decision = segment.querySelector("[data-role='decision']");
        const timerFill = segment.querySelector("[data-role='timer-fill']");
        const timerText = segment.querySelector("[data-role='timer-text']");
        let attempts = 0;
        let timer = null;

        function showStage(name) {
            ["setup", "choice", "halted", "passed"].forEach(n => { stage(n).hidden = n !== name; });
        }

        function stopTimer() {
            clearInterval(timer);
            timer = null;
        }

        function halt(reason) {
            stopTimer();
            attempts++;
            segment.querySelector("[data-role='halt-reason']").textContent = reason;
            showStage("halted");
        }

        segment.querySelector("[data-role='to-catalyst']").addEventListener("click", () => {
            showStage("choice");
            startButton.hidden = false;
            decision.hidden = true;
        });

        startButton.addEventListener("click", () => {
            startButton.hidden = true;
            decision.hidden = false;
            const endsAt = Date.now() + seconds * 1000;

            timer = setInterval(() => {
                const left = Math.max(0, endsAt - Date.now());
                timerFill.style.width = (left / (seconds * 1000) * 100) + "%";
                timerText.textContent = Math.ceil(left / 1000) + "s left";
                if (left === 0) {
                    halt("Time ran out. Under pressure, the default should still be the restorative "
                        + "choice: step back, mute, report or decline.");
                }
            }, 100);
        });

        segment.querySelectorAll(".choice-button").forEach(button => {
            button.addEventListener("click", () => {
                if (!timer) {
                    return;
                }
                if (button.dataset.compliant !== "true") {
                    halt(button.dataset.feedback);
                    return;
                }

                stopTimer();
                attempts++;
                points += attempts === 1 ? pointsFirst : pointsRetry;
                pointsDisplay.textContent = points;
                setHidden(`answer_${key}`, button.dataset.choice);
                setHidden(`attempts_${key}`, attempts);
                segment.querySelector("[data-role='pass-reason']").textContent =
                    button.dataset.feedback + ` (+${attempts === 1 ? pointsFirst : pointsRetry} Standing Points)`;
                showStage("passed");

                const next = segment.querySelector("[data-role='next']");
                if (index === segments.length - 1) {
                    next.hidden = true;
                    revealForm(page);
                }
            });
        });

        // Restart: back to the setup of this same segment
        segment.querySelector("[data-role='restart']").addEventListener("click", () => {
            timerFill.style.width = "100%";
            timerText.textContent = "";
            showStage("setup");
        });

        segment.querySelector("[data-role='next']").addEventListener("click", () => {
            segment.hidden = true;
            segments[index + 1].hidden = false;
            countDisplay.textContent = `${index + 2} / ${segments.length}`;
            segments[index + 1].scrollIntoView({ behavior: "smooth", block: "start" });
        });
    });
}

// ===== PHASE 4: PROBATIONARY CONTRACT =====
// Live counters only; the server enforces the same minimums.
function setupContract(page) {
    const form = page.querySelector("form");
    if (!form) {
        return;                            // review mode: contract already signed
    }

    const minPledges = Number(page.dataset.minPledges) || 2;
    const minReflection = Number(page.dataset.minReflection) || 80;
    const reflection = form.querySelector("#reflection");
    const submit = form.querySelector("[data-role='submit']");
    const pledgeCount = page.querySelector("[data-role='pledge-count']");
    const charCount = page.querySelector("[data-role='char-count']");

    function update() {
        const chosen = form.querySelectorAll("input[name='pledges']:checked").length;
        const length = reflection.value.trim().length;

        pledgeCount.textContent = `${chosen} selected (at least ${minPledges})`;
        charCount.textContent = `${length} / ${minReflection} characters minimum`;
        submit.disabled = chosen < minPledges || length < minReflection;
    }

    form.addEventListener("change", update);
    reflection.addEventListener("input", update);
    update();
}
