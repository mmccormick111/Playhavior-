package com.playhavior.model;

/**
 * Carries two values out of CategoryMappingService.mapReason(): the category and the pathway.
 * WHY a record: a method can only return one object; a record bundles both,
 *     is immutable, and Java generates the constructor and accessors.
 */
public record MappingResult(
        ViolationCategory category,
        PathwayCode pathway
) {
}
