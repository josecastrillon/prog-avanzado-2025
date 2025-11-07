package co.edu.uniremington.library.service.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for Loan responses.
 */
public class LoanResponse {
    private Long id;
    private String bookTitle;
    private String userName;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private Boolean active;

    /**
     * Default constructor required by JSON serialization frameworks (Jackson).
     * This allows Spring to automatically convert this object to JSON responses.
     */
    public LoanResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
