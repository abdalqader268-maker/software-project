package edu.najah.vrms.persistence;

import java.util.Optional;

import edu.najah.vrms.domain.Manager;

/**
 * Data access boundary for {@link Manager} accounts.
 * <p>
 * Belongs to the persistence layer; the service layer never touches the
 * storage mechanism directly.
 */
public interface ManagerRepository {

    /**
     * Looks a manager up by the unique login username.
     *
     * @param username the login username
     * @return the matching manager, or an empty optional when none exists
     */
    Optional<Manager> findByUsername(String username);

    /**
     * Stores a manager account.
     *
     * @param manager the account to persist
     */
    void save(Manager manager);
}
