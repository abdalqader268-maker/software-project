package edu.najah.vrms.validation;

/**
 * Strategy interface for a single rental validation rule.
 * <p>
 * Each business rule (duration limits, double-booking protection, ...) is
 * implemented as its own strategy. The {@link RentalValidator} runs every
 * configured rule before a rental is created, so new rules can be added in
 * later sprints without modifying existing code.
 */
public interface RentalValidationRule {

    /**
     * Validates one aspect of the rental request.
     *
     * @param request the rental request under validation
     * @throws RuntimeException a rule-specific exception when the request
     *                          violates this rule
     */
    void validate(RentalRequest request);
}
