package edu.najah.vrms.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite that runs a configurable chain of
 * {@link RentalValidationRule} strategies against a {@link RentalRequest}.
 * <p>
 * The service layer only depends on this class, so the set of business
 * rules can grow sprint by sprint without changing the rental workflow.
 */
public class RentalValidator {

    /** The ordered list of rules applied to every request. */
    private final List<RentalValidationRule> rules;

    /**
     * Creates a validator with the given rule chain.
     *
     * @param rules the rules to apply, evaluated in order
     */
    public RentalValidator(List<RentalValidationRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    /**
     * Runs every configured rule against the request. The first violated
     * rule aborts the validation by throwing its specific exception.
     *
     * @param request the rental request to validate
     */
    public void validate(RentalRequest request) {
        for (RentalValidationRule rule : rules) {
            rule.validate(request);
        }
    }
}
