package co.edu.uniremington.library.config;

import co.edu.uniremington.library.domain.exception.BookNotFoundException;
import co.edu.uniremington.library.domain.exception.LoanAlreadyReturnedException;
import co.edu.uniremington.library.domain.exception.LoanNotFoundException;
import co.edu.uniremington.library.domain.exception.NoAvailableCopiesException;
import co.edu.uniremington.library.domain.exception.UserNotFoundException;
import co.edu.uniremington.library.domain.exception.UserWithFinesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for GlobalExceptionHandler.
 *
 * TESTING EXCEPTION HANDLERS:
 * ===========================
 * These tests verify that exceptions are properly converted to HTTP responses
 * with the correct status codes and error messages.
 *
 * NEW: Validation error testing
 * =============================
 * The test for MethodArgumentNotValidException demonstrates how to verify
 * that validation errors are properly formatted and returned to the client.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("WHEN validation fails THEN HTTP 400 with error details")
    void whenValidationFails_thenHttp400WithErrorDetails() {
        // Given - Create a mock validation exception
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        // Simulate validation errors for bookId and userId
        FieldError bookIdError = new FieldError("loanRequest", "bookId", "Book ID is required");
        FieldError userIdError = new FieldError("loanRequest", "userId", "User ID must be a positive number");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(bookIdError, userIdError));

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

        // Then
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Book ID is required", response.getBody().get("bookId"));
        assertEquals("User ID must be a positive number", response.getBody().get("userId"));
    }

    @Test
    @DisplayName("WHEN UserWithFinesException THEN HTTP 400")
    void whenUserWithFinesException_thenHttp400() {
        // Given
        UserWithFinesException exception = new UserWithFinesException("User has fines");

        // When
        ResponseEntity<String> response = exceptionHandler.handleUserWithFines(exception);

        // Then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("User has fines", response.getBody());
    }

    @Test
    @DisplayName("WHEN BookNotFoundException THEN HTTP 404")
    void whenBookNotFoundException_thenHttp404() {
        // Given
        BookNotFoundException exception = new BookNotFoundException("Book not found");

        // When
        ResponseEntity<String> response = exceptionHandler.handleBookNotFound(exception);

        // Then
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Book not found", response.getBody());
    }

    @Test
    @DisplayName("WHEN UserNotFoundException THEN HTTP 404")
    void whenUserNotFoundException_thenHttp404() {
        // Given
        UserNotFoundException exception = new UserNotFoundException("User not found");

        // When
        ResponseEntity<String> response = exceptionHandler.handleUserNotFound(exception);

        // Then
        assertEquals(404, response.getStatusCode().value());
        assertEquals("User not found", response.getBody());
    }

    @Test
    @DisplayName("WHEN NoAvailableCopiesException THEN HTTP 400")
    void whenNoAvailableCopiesException_thenHttp400() {
        // Given
        NoAvailableCopiesException exception = new NoAvailableCopiesException("No available copies");

        // When
        ResponseEntity<String> response = exceptionHandler.handleNoAvailableCopies(exception);

        // Then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("No available copies", response.getBody());
    }

    @Test
    @DisplayName("WHEN LoanNotFoundException THEN HTTP 404")
    void whenLoanNotFoundException_thenHttp404() {
        // Given
        LoanNotFoundException exception = new LoanNotFoundException("Loan not found");

        // When
        ResponseEntity<String> response = exceptionHandler.handleLoanNotFound(exception);

        // Then
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Loan not found", response.getBody());
    }

    @Test
    @DisplayName("WHEN LoanAlreadyReturnedException THEN HTTP 400")
    void whenLoanAlreadyReturnedException_thenHttp400() {
        // Given
        LoanAlreadyReturnedException exception = new LoanAlreadyReturnedException("Loan already returned");

        // When
        ResponseEntity<String> response = exceptionHandler.handleLoanAlreadyReturned(exception);

        // Then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Loan already returned", response.getBody());
    }
}
