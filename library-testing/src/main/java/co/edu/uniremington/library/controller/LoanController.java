package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.service.LoanService;
import co.edu.uniremington.library.service.dto.LoanRequest;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.dto.LoanUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Loan operations.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Creates a new book loan.
     *
     * @param request LoanRequest DTO with validated bookId and userId
     * @return ResponseEntity with LoanResponse DTO
     */
    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest request) {
        // Service returns DTO directly - controller just passes it through
        LoanResponse response = loanService.createLoan(request.getBookId(), request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Processes a book return.
     *
     * @param id Loan ID to return
     * @return ResponseEntity with updated LoanResponse DTO
     */
    @PostMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable Long id) {
        // Service returns DTO directly - no mapping needed here
        LoanResponse response = loanService.returnBook(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates a loan using PATCH semantics (partial update).
     *
     * @param id      Loan ID to update
     * @param request Fields to update
     * @return ResponseEntity with updated LoanResponse DTO
     */
    @PatchMapping("/{id}")
    public ResponseEntity<LoanResponse> updateLoan(
            @PathVariable Long id,
            @RequestBody LoanUpdateRequest request) {

        LoanResponse response = loanService.updateLoan(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all loans.
     *
     * @return List of all loans as DTOs
     */
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        List<LoanResponse> loans = loanService.findAllLoans();
        return ResponseEntity.ok(loans);
    }

    /**
     * Gets all active loans.
     *
     * @return List of active loans as DTOs
     */
    @GetMapping("/active")
    public ResponseEntity<List<LoanResponse>> getActiveLoans() {
        List<LoanResponse> loans = loanService.findActiveLoans();
        return ResponseEntity.ok(loans);
    }

    /**
     * Gets all loans for a specific user.
     *
     * @param userId User ID
     * @return List of user's loans as DTOs
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getLoansByUser(@PathVariable Long userId) {
        List<LoanResponse> loans = loanService.findLoansByUser(userId);
        return ResponseEntity.ok(loans);
    }
}
