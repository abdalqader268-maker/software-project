package edu.najah.vrms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.najah.vrms.domain.Manager;
import edu.najah.vrms.domain.exception.AuthenticationException;
import edu.najah.vrms.domain.exception.UnauthorizedActionException;
import edu.najah.vrms.persistence.InMemoryManagerRepository;

/**
 * Unit tests for {@link AuthService} covering US1.1 (login) and US1.2
 * (logout / protected actions).
 */
class AuthServiceTest {

    /** Service under test. */
    private AuthService authService;

    /**
     * Seeds one known manager account before every test.
     */
    @BeforeEach
    void setUp() {
        InMemoryManagerRepository repository = new InMemoryManagerRepository();
        repository.save(new Manager("admin", "admin123", "Fleet Manager"));
        authService = new AuthService(repository);
    }

    @Test
    @DisplayName("US1.1 - valid credentials log the manager in")
    void loginWithValidCredentialsSucceeds() {
        Manager manager = authService.login("admin", "admin123");

        assertTrue(authService.isAuthenticated());
        assertEquals("admin", manager.getUsername());
        assertEquals("admin", authService.getCurrentManager().getUsername());
    }

    @Test
    @DisplayName("US1.1 - wrong password is rejected with an error message")
    void loginWithWrongPasswordFails() {
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authService.login("admin", "wrong-password"));

        assertEquals("Invalid username or password.", exception.getMessage());
        assertFalse(authService.isAuthenticated());
    }

    @Test
    @DisplayName("US1.1 - unknown username is rejected with an error message")
    void loginWithUnknownUsernameFails() {
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authService.login("ghost", "admin123"));

        assertEquals("Invalid username or password.", exception.getMessage());
        assertFalse(authService.isAuthenticated());
    }

    @Test
    @DisplayName("US1.2 - logout clears the session")
    void logoutClearsSession() {
        authService.login("admin", "admin123");

        authService.logout();

        assertFalse(authService.isAuthenticated());
        assertNull(authService.getCurrentManager());
    }

    @Test
    @DisplayName("US1.2 - protected actions require re-login after logout")
    void protectedActionAfterLogoutIsRejected() {
        authService.login("admin", "admin123");
        authService.logout();

        assertThrows(UnauthorizedActionException.class,
                authService::requireAuthentication);
    }
}
