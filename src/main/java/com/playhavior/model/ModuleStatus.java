package com.playhavior.model;

/**
 * Where a module is in the player's progress. The pathway page picks the
 * icon from it; LearningPathway counts COMPLETED ones for the progress bar.
 */
public enum ModuleStatus {
    AVAILABLE,
    LOCKED,
    COMPLETED
}
