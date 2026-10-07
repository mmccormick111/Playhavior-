package com.playhavior.service;

/**
 * Thrown when a module submission does not meet its completion rules
 * (e.g. a decision did not end on the restorative choice, or the reflection is too short).
 * CAUGHT BY: ModuleController, which shows the message on the module page.
 */
public class InvalidSubmissionException extends RuntimeException {

    public InvalidSubmissionException(String message) {
        super(message);
    }
}
