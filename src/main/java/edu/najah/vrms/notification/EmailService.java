package edu.najah.vrms.notification;

/**
 * Boundary interface of the outgoing e-mail channel.
 * <p>
 * Kept deliberately small so it can be mocked with Mockito in unit tests
 * and replaced by a real SMTP implementation later without touching the
 * business logic.
 */
public interface EmailService {

    /**
     * Sends one e-mail message.
     *
     * @param to      recipient e-mail address
     * @param subject subject line of the message
     * @param body    plain-text body of the message
     */
    void sendEmail(String to, String subject, String body);
}
