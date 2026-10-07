package com.playhavior.content;

import com.playhavior.model.ModuleType;
import com.playhavior.model.ReintegrationTrack;
import com.playhavior.model.RiskLevel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * All the teaching content for the interactive modules: chat rounds, risk scenarios,
 * sandbox decision points and contract pledges, for both tracks.
 *
 * FRAMEWORK: Mayer's CTML + Mezirow's Transformative Learning + Restorative Justice.
 * USED BY: ModulePlanService (step counts), ModuleProgressService (grading),
 *          ModuleController (to render the pages), AppealReportService (pledge texts).
 * WHY in code, not the database: the content is fixed and versioned with the app;
 *     the database only stores each player's PROGRESS through it. Moving this to a
 *     CSV or tables later only changes this class.
 * NOTE: the scenarios use made-up player names and never contain slurs; the
 *       toxic lines are realistic but kept non-graphic.
 */
@Component
public class ModuleContentCatalog {

    // ===== VALIDATION HUB (Phase 1) =====

    // The only correct click: the card showing the flagged behaviour
    public static final String FLAGGED_BEHAVIOR_KEY = "flagged";
    public static final String HUB_QUESTION_KEY = "hub";

    // ===== PERSPECTIVE SHIFT: social track (Phase 2A) =====

    // Both bars start here, out of 100
    public static final int STARTING_BAR_VALUE = 70;

    private static final List<ChatRound> CHAT_ROUNDS = List.of(
            new ChatRound(
                    "round1",
                    "The Struggling Teammate",
                    "You are on a five-player team. Your teammate Kai has been eliminated "
                            + "first in three rounds in a row. The score is 1-3.",
                    List.of(
                            new ChatLine("Kai", "sorry guys, my aim is way off today", false),
                            new ChatLine("Riley", "bruh", true),
                            new ChatLine("System", "Round 5 starting", false)
                    ),
                    "What do you type in team chat?",
                    List.of(
                            new ChatOption("r1a", "\"Uninstall. You're the reason we're losing.\"",
                                    false, -35, -20,
                                    "Kai tilts and starts playing scared, and two teammates go quiet. "
                                            + "Blame moves the whole team's focus from the game to one person."),
                            new ChatOption("r1b", "\"Stop feeding, seriously.\"",
                                    false, -15, -10,
                                    "Short jabs still land as hostile. Text has no tone of voice to "
                                            + "soften it, so the team hears frustration, not a plan."),
                            new ChatOption("r1c", "\"No worries Kai, stick with me mid and I'll trade for you.\"",
                                    true, 20, 15,
                                    "A calm, specific plan gives a struggling player something to do "
                                            + "instead of something to fear. Morale and coordination go up."),
                            new ChatOption("r1d", "Say nothing, mute chat and focus on my own game.",
                                    true, 0, 5,
                                    "Stepping back is a legitimate choice: it protects your focus "
                                            + "without adding pressure to anyone else.")
                    )
            ),
            new ChatRound(
                    "round2",
                    "Comms Breakdown",
                    "Your team just lost a close round because Sam missed your callout. "
                            + "An opponent types in all-chat.",
                    List.of(
                            new ChatLine("Sam", "my bad, didn't hear the call", false),
                            new ChatLine("Opponent", "ez", true)
                    ),
                    "How do you respond?",
                    List.of(
                            new ChatOption("r2a", "\"Are you deaf? Learn to listen or get out of my lobby.\"",
                                    false, -30, -15,
                                    "Sam already owned the mistake. Piling on punishes honesty, so "
                                            + "next time the team hides mistakes instead of fixing them."),
                            new ChatOption("r2b", "Reply to the opponent: \"talk when you're good, trash.\"",
                                    false, -10, -15,
                                    "Arguing with an opponent is exactly what the taunt was for. "
                                            + "Now your attention is on all-chat, not the next round."),
                            new ChatOption("r2c", "\"All good, I'll ping it on the map next time so it's clearer.\"",
                                    true, 15, 10,
                                    "You turned a mistake into a fix. The team trusts that errors "
                                            + "get solved, not punished."),
                            new ChatOption("r2d", "Ignore the \"ez\" and call the next play.",
                                    true, 10, 15,
                                    "Not taking the bait keeps the team on task. The taunt has nothing "
                                            + "to feed on.")
                    )
            ),
            new ChatRound(
                    "round3",
                    "The Other Seat",
                    "Now you are the target. You are having an off game and a teammate, Jax, "
                            + "starts on you.",
                    List.of(
                            new ChatLine("Jax", "worst player I've ever been matched with", true),
                            new ChatLine("Jax", "just quit already", true),
                            new ChatLine("Jax", "everyone report this guy for throwing", true)
                    ),
                    "You are on the receiving end now. What do you do?",
                    List.of(
                            new ChatOption("r3a", "Fire back: \"Says the guy with two kills, clown.\"",
                                    false, -25, -15,
                                    "Retaliation keeps the fight going and pulls two players off task. "
                                            + "Notice how it felt to read Jax's messages: that is what "
                                            + "the other side of your notice felt like."),
                            new ChatOption("r3b", "Quit the match.",
                                    false, -20, -30,
                                    "Leaving punishes the three teammates who did nothing wrong, "
                                            + "and it can count as a penalty on your own account."),
                            new ChatOption("r3c", "Mute Jax, report the abuse with the in-game tool, keep playing.",
                                    true, 15, 10,
                                    "This is the restorative path: it protects you, sends the behaviour "
                                            + "to the people whose job is to handle it, and keeps the match alive."),
                            new ChatOption("r3d", "\"gg, let's just focus on the next round.\"",
                                    true, 10, 5,
                                    "A calm reset gives the rest of the team permission to move on.")
                    )
            )
    );

    // ===== RISK MATRIX: system integrity track (Phase 2B) =====

    // Narrated intro (Modality Principle): read aloud by the browser's speech engine
    public static final String RISK_MATRIX_NARRATION =
            "Cheats, exploits and phishing links never affect just one match. "
                    + "Third-party software injects code into the game client, so the servers spend "
                    + "resources detecting it and every match it touches is no longer fair. "
                    + "Exploits break the rules the game economy and matchmaking depend on. "
                    + "Phishing steals real people's accounts, purchases and progress. "
                    + "Each one chips away at the trust that makes players want to stay.";

    private static final List<RiskScenario> RISK_SCENARIOS = List.of(
            new RiskScenario("risk1",
                    "A friend offers to log into your account and play ranked for you to boost your rank.",
                    RiskLevel.SUSPENSION_RISK,
                    "Account sharing and boosting break the terms of service on every major platform, "
                            + "and the penalty lands on YOUR account, not your friend's."),
            new RiskScenario("risk2",
                    "You find a collision glitch that lets you see through walls in ranked matches.",
                    RiskLevel.SUSPENSION_RISK,
                    "Knowingly exploiting a bug for an advantage counts as cheating, even without any "
                            + "extra software. The legitimate move is to report it."),
            new RiskScenario("risk3",
                    "You install a third-party \"aim assist\" script that runs alongside the game.",
                    RiskLevel.PERMANENT_BAN_RISK,
                    "Third-party cheat software is what anti-cheat systems look for. It usually means a "
                            + "permanent ban, sometimes on the whole device."),
            new RiskScenario("risk4",
                    "You send friends a \"free skins\" link that asks them to sign in with their platform login.",
                    RiskLevel.PERMANENT_BAN_RISK,
                    "That is phishing: it steals accounts. Platforms treat credential theft as one of the "
                            + "most serious violations, and it can be a crime."),
            new RiskScenario("risk5",
                    "You buy in-game currency from a cheap third-party website.",
                    RiskLevel.SUSPENSION_RISK,
                    "Grey-market currency is often bought with stolen cards. Accounts that receive it can "
                            + "be suspended or have their items wiped."),
            new RiskScenario("risk6",
                    "You report a duplication bug through the game's official bug-report form and never use it.",
                    RiskLevel.LEGITIMATE,
                    "Reporting through official channels is exactly what developers want; some studios "
                            + "even reward it."),
            new RiskScenario("risk7",
                    "You remap your controls using the platform's built-in accessibility settings.",
                    RiskLevel.LEGITIMATE,
                    "Official settings and accessibility tools are always allowed.")
    );

    // ===== ACCOUNTABILITY SANDBOX (Phase 3) =====

    // The strict timer for every decision point
    public static final int SANDBOX_SECONDS_PER_DECISION = 10;

    // Standing Points: full points for a first-try restorative choice, half after a restart
    public static final int POINTS_FIRST_TRY = 100;
    public static final int POINTS_AFTER_RETRY = 50;

    private static final List<SandboxSegment> SOCIAL_SANDBOX = List.of(
            new SandboxSegment("seg1", "Falsely Accused",
                    "Final round, the score is tied and everything rides on this round.",
                    "A teammate types: \"we're losing because of YOU, you're throwing.\" You are not throwing.",
                    List.of(
                            new SandboxChoice("s1a", "Fire back with insults about their play.", false,
                                    "Insults in a match-deciding moment are the exact behaviour your "
                                            + "restriction is for. A false accusation does not make it okay."),
                            new SandboxChoice("s1b", "Mute them and call the next play for the team.", true,
                                    "You protected your focus and kept leading. The accusation has "
                                            + "nothing to feed on."),
                            new SandboxChoice("s1c", "Type \"report me then, idiot.\"", false,
                                    "Daring someone while insulting them keeps the conflict alive and "
                                            + "is still harassment.")
                    )),
            new SandboxSegment("seg2", "The Taunt",
                    "You just lost a close round to the other team.",
                    "An opponent spams taunts in all-chat and emotes over your character.",
                    List.of(
                            new SandboxChoice("s2a", "Reply with the harshest insult you can think of.", false,
                                    "Matching their toxicity makes two reports instead of one, and only "
                                            + "yours is the subject of an appeal."),
                            new SandboxChoice("s2b", "Report the taunting with the in-game tool and keep playing.", true,
                                    "Reporting hands the behaviour to moderators. You stay in control of "
                                            + "your own conduct."),
                            new SandboxChoice("s2c", "Follow them into the post-match lobby to keep arguing.", false,
                                    "Following someone to continue a fight is targeted harassment, even "
                                            + "if they started it.")
                    )),
            new SandboxSegment("seg3", "After the Loss",
                    "The match is over and your team lost.",
                    "A teammate who played badly writes: \"sorry team, that one's on me.\"",
                    List.of(
                            new SandboxChoice("s3a", "\"Yeah it is. Don't queue ranked again.\"", false,
                                    "They took responsibility; punishing that teaches people to hide "
                                            + "mistakes. This is the moment to model accountability."),
                            new SandboxChoice("s3b", "\"gg, everyone has off games. We'll get the next one.\"", true,
                                    "Restorative: you acknowledged the loss without blame and kept the "
                                            + "community welcoming."),
                            new SandboxChoice("s3c", "Message them privately later to keep criticising.", false,
                                    "Moving criticism into private messages is still harassment, and "
                                            + "often feels worse to receive.")
                    ))
    );

    private static final List<SandboxSegment> INTEGRITY_SANDBOX = List.of(
            new SandboxSegment("seg1", "The Economy Bug",
                    "You are trading in the in-game market before a big update.",
                    "Clicking \"sell\" twice duplicates a rare item. Anyone could do it right now.",
                    List.of(
                            new SandboxChoice("s1a", "Duplicate as many items as possible before the patch.", false,
                                    "Exploiting the economy damages every other player's items and is "
                                            + "treated as cheating. Platforms roll back and ban for it."),
                            new SandboxChoice("s1b", "Stop, report it through the official bug channel, and don't use it.", true,
                                    "You protected the economy and your account. This is exactly what "
                                            + "developers ask players to do."),
                            new SandboxChoice("s1c", "Tell your squad so everyone can farm it together.", false,
                                    "Spreading an exploit multiplies the damage and puts your friends' "
                                            + "accounts at risk too.")
                    )),
            new SandboxSegment("seg2", "The Verification Link",
                    "You get a direct message from an account with an official-looking name.",
                    "\"Verify your account in 10 minutes to claim a free legendary skin\" with a sign-in link.",
                    List.of(
                            new SandboxChoice("s2a", "Click the link and sign in quickly before it expires.", false,
                                    "Urgency is the classic phishing trick. Signing in hands over your "
                                            + "account, payment details and progress."),
                            new SandboxChoice("s2b", "Ignore the link, report the account, and block it.", true,
                                    "Real platforms never ask you to sign in through a message. Reporting "
                                            + "protects the next person it targets."),
                            new SandboxChoice("s2c", "Forward it to friends so they get the skin too.", false,
                                    "Forwarding a phishing link spreads the attack; that is how account "
                                            + "theft rings grow.")
                    )),
            new SandboxSegment("seg3", "The \"Undetectable\" Script",
                    "You are one win away from a new rank before the season ends.",
                    "A teammate offers you an \"undetectable\" aim script for ranked.",
                    List.of(
                            new SandboxChoice("s3a", "Try it in one match. Just to see.", false,
                                    "One match is enough for anti-cheat to flag the account. \"Just once\" "
                                            + "is how most permanent bans start."),
                            new SandboxChoice("s3b", "Decline, and report the offer.", true,
                                    "You kept the rank honest and protected the matches of everyone you "
                                            + "would have played against."),
                            new SandboxChoice("s3c", "Buy it for later, but don't use it yet.", false,
                                    "Buying cheats funds the people who make them and is itself against "
                                            + "most platforms' terms.")
                    ))
    );

    // ===== PROBATIONARY CONTRACT (Phase 4) =====

    public static final int MIN_PLEDGES = 2;
    public static final int MIN_REFLECTION_LENGTH = 80;

    private static final List<Pledge> SOCIAL_PLEDGES = List.of(
            new Pledge("mute_enemy_chat", "Automatically mute enemy text chat for 14 days"),
            new Pledge("match_limit", "Set a daily maximum of 3 ranked matches to mitigate tilt"),
            new Pledge("quick_chat_only", "Use pings and quick-chat only (no free-text chat) for 7 days"),
            new Pledge("loss_break", "Take a 10-minute break after two losses in a row"),
            new Pledge("report_not_respond", "Report toxic players instead of responding to them")
    );

    private static final List<Pledge> INTEGRITY_PLEDGES = List.of(
            new Pledge("enable_2fa", "Turn on two-factor authentication for my account"),
            new Pledge("no_account_sharing", "Never share my login, even with friends"),
            new Pledge("report_exploits", "Report exploits through the official bug channel instead of using them"),
            new Pledge("remove_third_party", "Uninstall third-party tools or overlays the platform has not approved"),
            new Pledge("official_store_only", "Buy items and currency only through the official store")
    );

    // ===== PUBLIC LOOKUPS =====

    public List<ChatRound> chatRounds() {
        return CHAT_ROUNDS;
    }

    public List<RiskScenario> riskScenarios() {
        return RISK_SCENARIOS;
    }

    public List<SandboxSegment> sandboxSegments(ReintegrationTrack track) {
        return track == ReintegrationTrack.SYSTEM_INTEGRITY
                ? INTEGRITY_SANDBOX
                : SOCIAL_SANDBOX;
    }

    public List<Pledge> pledges(ReintegrationTrack track) {
        return track == ReintegrationTrack.SYSTEM_INTEGRITY
                ? INTEGRITY_PLEDGES
                : SOCIAL_PLEDGES;
    }

    public String reflectionPrompt(ReintegrationTrack track) {
        return track == ReintegrationTrack.SYSTEM_INTEGRITY
                ? "In your own words: what did you do, who did it put at risk, and what will you do differently?"
                : "In your own words: what happened, how did it affect the other players, and what will you do differently?";
    }

    // Number of graded steps in a module (shown as "N activities" on the pathway page)
    public int stepCount(ModuleType type, ReintegrationTrack track) {
        return switch (type) {
            case VALIDATION_HUB -> 1;
            case PERSPECTIVE_SHIFT -> CHAT_ROUNDS.size();
            case RISK_MATRIX -> RISK_SCENARIOS.size();
            case ACCOUNTABILITY_SANDBOX -> sandboxSegments(track).size();
            case PROBATIONARY_CONTRACT -> 2;   // choose pledges + write reflection
        };
    }

    /*
     * The least time (seconds) a player who actually reads the module would need.
     * Used for "Reading/Engagement Velocity": finishing far faster than this
     * suggests speed-clicking rather than reading.
     */
    public int minimumEngagedSeconds(ModuleType type) {
        return switch (type) {
            case VALIDATION_HUB -> 20;
            case PERSPECTIVE_SHIFT, RISK_MATRIX -> 60;
            case ACCOUNTABILITY_SANDBOX -> 30;
            case PROBATIONARY_CONTRACT -> 45;
        };
    }

    // Text for a pledge key (used in the appeal packet)
    public String pledgeText(String key) {
        return Stream.concat(SOCIAL_PLEDGES.stream(), INTEGRITY_PLEDGES.stream())
                .filter(pledge -> pledge.key().equals(key))
                .map(Pledge::text)
                .findFirst()
                .orElse(key);
    }
}
