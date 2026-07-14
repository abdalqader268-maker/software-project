package edu.najah.vrms.domain;

/**
 * Represents a system manager who is allowed to operate the rental system.
 * <p>
 * Managers must authenticate through the
 * {@link edu.najah.vrms.service.AuthService} before performing any protected
 * action such as renting out a vehicle.
 */
public class Manager {

    /** Unique username used to identify the manager at login. */
    private final String username;

    /** Secret password used to verify the manager's identity. */
    private final String password;

    /** Human friendly display name of the manager. */
    private final String fullName;

    /**
     * Creates a new manager account.
     *
     * @param username unique login username, must not be {@code null}
     * @param password login password, must not be {@code null}
     * @param fullName display name of the manager
     */
    public Manager(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    /**
     * Returns the unique login username of this manager.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Checks whether the supplied password matches this manager's password.
     *
     * @param candidate the password entered at login
     * @return {@code true} when the password matches, {@code false} otherwise
     */
    public boolean passwordMatches(String candidate) {
        return password.equals(candidate);
    }

    /**
     * Returns the display name of this manager.
     *
     * @return the manager's full name
     */
    public String getFullName() {
        return fullName;
    }
}
