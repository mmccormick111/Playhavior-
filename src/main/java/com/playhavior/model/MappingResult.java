package com.playhavior.model;

/**
 * Carries the three mapping decisions out of CategoryMappingService.mapReason():
 * the category, the pathway code and the reintegration track.
 * WHY a record: a method can only return one object; a record bundles them,
 *     is immutable, and Java generates the constructor and accessors.
 */
public record MappingResult(
        ViolationCategory category,
        PathwayCode pathway,
        ReintegrationTrack track
) {
}
