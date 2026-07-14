package edu.najah.vrms.service;

import java.util.Optional;

import edu.najah.vrms.domain.Manager;
import edu.najah.vrms.domain.exception.AuthenticationException;
import edu.najah.vrms.domain.exception.UnauthorizedActionException;
import edu.najah.vrms.persistence.ManagerRepository;

/**
 * Application service handling manager authentication (US1.1, US1.2).
 * <p>
 * The service keeps track of the currently logged-in manager. Protected
 * services call {@link #requireAuthentication()} before executing, so once
 * {@link #logout()} runs, every protected action fails until the manager
 * logs in again.
 */
public class AuthService {

    /** Repository used to resolve manager accounts. */
    private final ManagerRepository managerRepository;

    /** The manager of the current session, {@code null} when logged out. */
    private Manager currentManager;

    /**
     * Creates the service.
     *
     * @param managerRepository repository holding the manager accounts
     */
    public AuthService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    /**
     * Attempts to log a manager in (US1.1).
     *
     * @param username the entered username
     * @param password the entered password
     * @return the authenticated {@link Manager}
     * @throws AuthenticationException when the username is unknown or the
     *                                 password does not match
     */
    public Manager login(String username, String password) {
        Optional<Manager> manager = managerRepository.findByUsername(username);
        if (manager.isEmpty() || !manager.get().passwordMatches(password)) {
            throw new AuthenticationException("Invalid username or password.");
        }
        this.currentManager = manager.get();
        return currentManager;
    }

    /**
     * Ends the current session (US1.2). Protected actions executed after
     * this call are rejected until the next successful login.
     */
    public void logout() {
        this.currentManager = null;
    }

    /**
     * Tells whether a manager is currently logged in.
     *
     * @return {@code true} when a session is active
     */
    public boolean isAuthenticated() {
        return currentManager != null;
    }

    /**
     * Returns the manager of the current session.
     *
     * @return the logged-in manager, or {@code null} when nobody is logged in
     */
    public Manager getCurrentManager() {
        return currentManager;
    }

    /**
     * Guard used by protected services.
     *
     * @throws UnauthorizedActionException when no manager is logged in
     */
    public void requireAuthentication() {
        if (!isAuthenticated()) {
            throw new UnauthorizedActionException(
                    "This action requires a logged-in manager. Please login first.");
        }
    }
}
