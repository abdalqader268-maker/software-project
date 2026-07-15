package edu.najah.vrms.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import edu.najah.vrms.TestFixtures;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.exception.InvalidRentalPeriodException;

/**
 * Unit tests for the {@link DurationLimitRule} strategy (US2.3).
 * <p>
 * The current date is provided by a Mockito-mocked {@link Clock}, which
 * satisfies the course requirement of mocking date/time handling services.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DurationLimitRuleTest {

    /** Frozen "today" returned by the mocked clock: 2026-07-14. */
    private static final LocalDate TODAY = LocalDate.of(2026, 7, 14);

    /** Mocked date/time source. */
    @Mock
    private Clock clock;

    /** Rule under test, configured with limits 1..30 days. */
    private DurationLimitRule rule;

    /**
     * Programs the mocked clock to always report the frozen date.
     */
    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        rule = new DurationLimitRule(clock, 1, 30);
    }

    /**
     * Builds a request for the shared test vehicle and the given period.
     *
     * @param start requested start date
     * @param end   requested end date
     * @return the rental request
     */
    private RentalRequest request(LocalDate start, LocalDate end) {
        Vehicle vehicle = TestFixtures.availableCorolla();
        return new RentalRequest(vehicle, "Ahmad Ali", "ahmad@example.com", start, end);
    }

    @Test
    @DisplayName("A period inside the limits passes")
    void validPeriodPasses() {
        assertDoesNotThrow(() -> rule.validate(request(TODAY, TODAY.plusDays(7))));
    }

    @Test
    @DisplayName("The maximum allowed length passes")
    void maximumLengthPasses() {
        assertDoesNotThrow(() -> rule.validate(request(TODAY, TODAY.plusDays(30))));
    }

    @Test
    @DisplayName("US2.3 - end before start is rejected")
    void endBeforeStartIsRejected() {
        assertThrows(InvalidRentalPeriodException.class,
                () -> rule.validate(request(TODAY.plusDays(3), TODAY)));
    }

    @Test
    @DisplayName("US2.3 - zero-day rental is rejected")
    void zeroDayRentalIsRejected() {
        assertThrows(InvalidRentalPeriodException.class,
                () -> rule.validate(request(TODAY, TODAY)));
    }

    @Test
    @DisplayName("US2.3 - period longer than the maximum is rejected")
    void tooLongPeriodIsRejected() {
        assertThrows(InvalidRentalPeriodException.class,
                () -> rule.validate(request(TODAY, TODAY.plusDays(31))));
    }

    @Test
    @DisplayName("US2.3 - start date in the past is rejected")
    void pastStartIsRejected() {
        assertThrows(InvalidRentalPeriodException.class,
                () -> rule.validate(request(TODAY.minusDays(1), TODAY.plusDays(1))));
    }

    @Test
    @DisplayName("US2.3 - missing dates are rejected")
    void missingDatesAreRejected() {
        assertThrows(InvalidRentalPeriodException.class,
                () -> rule.validate(request(null, TODAY.plusDays(1))));
        assertThrows(InvalidRentalPeriodException.class,
                () -> rule.validate(request(TODAY, null)));
    }
}
