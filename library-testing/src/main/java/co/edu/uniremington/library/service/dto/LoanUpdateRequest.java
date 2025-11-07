package co.edu.uniremington.library.service.dto;

/**
 * DTO for updating loan fields via PATCH endpoint.
 */
public class LoanUpdateRequest {

    /**
     * Whether the loan is active.
     * Setting to false effectively "returns" the book.
     */
    private Boolean active;

    /**
     * New return date (for extending loans).
     */
    private String returnDate; // Using String for simplicity, parse to LocalDate in service

    // Constructors
    public LoanUpdateRequest() {
    }

    public LoanUpdateRequest(Boolean active) {
        this.active = active;
    }

    // Getters and Setters
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
