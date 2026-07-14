package edu.najah.vrms.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;

/**
 * Unit tests for {@link EmailReminderObserver} (US3.1).
 * <p>
 * The outgoing {@link EmailService} is a Mockito mock, so no real e-mail
 * infrastructure is needed during testing.
 */
@ExtendWith(MockitoExtension.class)
class EmailReminderObserverTest {

    /** Mocked e-mail channel. */
    @Mock
    private EmailService emailService;

    /** Captures the body handed to the e-mail channel. */
    @Captor
    private ArgumentCaptor<String> bodyCaptor;

    @Test
    @DisplayName("US3.1 - an expiry event produces one reminder e-mail to the customer")
    void expiryEventProducesReminderEmail() {
        Vehicle vehicle = new Vehicle("V-1", "NAB-1234", "Toyota", "Corolla",
                new BigDecimal("35.00"), VehicleStatus.RENTED);
        Rental rental = new Rental("R-1", vehicle, "Ahmad Ali", "ahmad@example.com",
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 16));
        EmailReminderObserver observer = new EmailReminderObserver(emailService);

        observer.onRentalExpiring(rental, 2);

        verify(emailService).sendEmail(eq("ahmad@example.com"), anyString(),
                bodyCaptor.capture());
        String body = bodyCaptor.getValue();
        assertTrue(body.contains("Ahmad Ali"), "body should greet the customer");
        assertTrue(body.contains("NAB-1234"), "body should mention the plate number");
        assertTrue(body.contains("2026-07-16"), "body should mention the end date");
        assertTrue(body.contains("2 day(s) left"), "body should mention the days left");
    }

    @Test
    @DisplayName("US3.1 - a rental expiring today is worded accordingly")
    void expiryTodayIsWordedAccordingly() {
        Vehicle vehicle = new Vehicle("V-1", "NAB-1234", "Toyota", "Corolla",
                new BigDecimal("35.00"), VehicleStatus.RENTED);
        Rental rental = new Rental("R-1", vehicle, "Ahmad Ali", "ahmad@example.com",
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 14));
        EmailReminderObserver observer = new EmailReminderObserver(emailService);

        observer.onRentalExpiring(rental, 0);

        verify(emailService).sendEmail(eq("ahmad@example.com"), anyString(),
                bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("today"),
                "body should say the rental ends today");
        assertEquals("ahmad@example.com", rental.getCustomerEmail());
    }
}
