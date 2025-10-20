package co.edu.uniremington.library.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for creating a book loan.
 */
public class LoanRequest {

    @NotNull(message = "Book ID is required")
    @Positive(message = "Book ID must be a positive number")
    private Long bookId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    /**
     * Default constructor required by JSON deserialization frameworks.
     */
    public LoanRequest() {
    }

    /**
     * Full constructor for testing and manual creation.
     *
     * @param bookId ID of the book to loan (must be > 0)
     * @param userId ID of the user requesting the loan (must be > 0)
     */
    public LoanRequest(Long bookId, Long userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
