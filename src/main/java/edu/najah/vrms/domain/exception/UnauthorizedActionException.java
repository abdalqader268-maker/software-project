package edu.najah.vrms.domain.exception;

/**
 * Thrown when a protected action is attempted while no manager is logged in,
 * for example after a logout.
 */
public class UnauthorizedActionException extends RuntimeException {

    /**
     * Creates the exception with a human readable error message.
     *
     * @param message description of the rejected action
     */
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
