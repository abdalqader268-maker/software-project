package edu.najah.vrms.domain.exception;

/**
 * Thrown when an electric vehicle cannot be rented because its battery charge
 * is below the minimum level required to hand it to a customer (US5.2).
 */
public class LowBatteryException extends RuntimeException {

    /**
     * Creates the exception with a human readable error message.
     *
     * @param message description of the failed battery check
     */
    public LowBatteryException(String message) {
        super(message);
    }
}
