package edu.najah.vrms.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Immutable billing summary produced when a vehicle is returned (Sprint&nbsp;4).
 * <p>
 * It records the base rental cost (US4.2), any late-return penalty (US4.3) and
 * their total, together with the day the vehicle was actually returned.
 */
public class RentalReceipt {

    /** Id of the rental this receipt bills. */
    private final String rentalId;

    /** Day the vehicle was actually returned. */
    private final LocalDate returnDate;

    /** Base cost for the booked duration. */
    private final BigDecimal baseCost;

    /** Penalty charged for returning late; zero when on time. */
    private final BigDecimal lateFee;

    /**
     * Creates a receipt.
     *
     * @param rentalId   id of the billed rental
     * @param returnDate day the vehicle was returned
     * @param baseCost   base rental cost
     * @param lateFee    late-return penalty (zero when on time)
     */
    public RentalReceipt(String rentalId, LocalDate returnDate,
                         BigDecimal baseCost, BigDecimal lateFee) {
        this.rentalId = rentalId;
        this.returnDate = returnDate;
        this.baseCost = baseCost;
        this.lateFee = lateFee;
    }

    /**
     * Returns the id of the billed rental.
     *
     * @return the rental id
     */
    public String getRentalId() {
        return rentalId;
    }

    /**
     * Returns the day the vehicle was returned.
     *
     * @return the return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Returns the base rental cost (US4.2).
     *
     * @return the base cost
     */
    public BigDecimal getBaseCost() {
        return baseCost;
    }

    /**
     * Returns the late-return penalty (US4.3).
     *
     * @return the late fee, zero when the vehicle was returned on time
     */
    public BigDecimal getLateFee() {
        return lateFee;
    }

    /**
     * Returns the total amount owed: base cost plus late fee.
     *
     * @return the grand total
     */
    public BigDecimal getTotal() {
        return baseCost.add(lateFee);
    }
}
