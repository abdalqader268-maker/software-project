package edu.najah.vrms.notification;

/**
 * Development implementation of {@link EmailService} that prints the
 * message to the console instead of talking to a real mail server.
 * <p>
 * Used by the demo application; unit tests replace the interface with a
 * Mockito mock instead.
 */
public class ConsoleEmailService implements EmailService {

    /** {@inheritDoc} */
    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("--- EMAIL ---");
        System.out.println("To     : " + to);
        System.out.println("Subject: " + subject);
        System.out.println(body);
        System.out.println("-------------");
    }
}
