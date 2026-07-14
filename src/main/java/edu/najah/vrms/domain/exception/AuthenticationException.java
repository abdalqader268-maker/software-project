package edu.najah.vrms.domain.exception;

/**
 * Thrown when a login attempt fails because the supplied credentials do not
 * match any registered manager.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Creates the exception with a human readable error message.
     *
     * @param message description of the failed login attempt
     */
    public AuthenticationException(String message) {
        super(message);
    }
}
