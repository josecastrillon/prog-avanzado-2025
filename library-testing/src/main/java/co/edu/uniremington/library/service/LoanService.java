package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.LoanAlreadyReturnedException;
import co.edu.uniremington.library.domain.exception.LoanNotFoundException;
import co.edu.uniremington.library.domain.exception.NoAvailableCopiesException;
import co.edu.uniremington.library.domain.exception.UserWithFinesException;
import co.edu.uniremington.library.domain.model.Book;
import co.edu.uniremington.library.domain.model.Loan;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.LoanRepository;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.dto.LoanUpdateRequest;
import co.edu.uniremington.library.service.mapper.LoanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for Loan-related operations.
 */
@Service
@Transactional
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final UserService userService;
    private final LoanMapper loanMapper;

    public LoanService(LoanRepository loanRepository,
                       BookService bookService,
                       UserService userService,
                       LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.loanMapper = loanMapper;
    }

    /**
     * Creates a new book loan for a user.
     *
     * @param bookId ID of the book to loan
     * @param userId ID of the user requesting the loan
     * @return LoanResponse DTO with loan details
     * @throws co.edu.uniremington.library.domain.exception.UserNotFoundException if user doesn't exist
     * @throws UserWithFinesException                                             if user has pending fines
     * @throws co.edu.uniremington.library.domain.exception.BookNotFoundException if book doesn't exist
     * @throws NoAvailableCopiesException                                         if no copies available
     */
    public LoanResponse createLoan(Long bookId, Long userId) {
        // 1. Validate user exists (delegates to UserService)
        // Using findEntityById() - package-private method for internal service-to-service communication
        User user = userService.findEntityById(userId);

        // 2. Validate user doesn't have fines (delegates to UserService)
        if (userService.hasFines(user)) {
            throw new UserWithFinesException("User has pending fines");
        }

        // 3. Validate book exists (delegates to BookService)
        // Using findEntityById() - package-private method for internal service-to-service communication
        Book book = bookService.findEntityById(bookId);

        // 4. Validate book has available copies (delegates to BookService)
        if (!bookService.hasAvailableCopies(book)) {
            throw new NoAvailableCopiesException("No available copies of the book: " + book.getTitle());
        }

        // 5. Create the loan
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(14));  // 2 weeks loan period
        loan.setActive(true);

        // 6. Update available copies (delegates to BookService)
        bookService.decrementAvailableCopies(book);

        // 7. Save loan and convert to DTO
        Loan savedLoan = loanRepository.save(loan);

        // MAPPING: Entity → DTO
        return loanMapper.toResponse(savedLoan);
    }

    /**
     * Processes the return of a loaned book.
     *
     * @param loanId ID of the loan to return
     * @return LoanResponse DTO with updated loan details
     * @throws LoanNotFoundException        if loan doesn't exist
     * @throws LoanAlreadyReturnedException if loan is already returned
     */
    public LoanResponse returnBook(Long loanId) {
        // 1. Validate loan exists
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // 2. Validate loan is active (IDEMPOTENCY CHECK)
        if (!loan.getActive()) {
            throw new LoanAlreadyReturnedException("Loan with ID " + loanId + " has already been returned");
        }

        // 3. Mark loan as inactive
        loan.setActive(false);

        // 4. Increment available copies (delegates to BookService)
        Book book = loan.getBook();
        bookService.incrementAvailableCopies(book);

        // 5. Save updated loan and convert to DTO
        Loan updatedLoan = loanRepository.save(loan);

        // MAPPING: Entity → DTO
        return loanMapper.toResponse(updatedLoan);
    }

    /**
     * Updates a loan using PATCH semantics (partial update).
     *
     * @param loanId  ID of the loan to update
     * @param request Fields to update
     * @return Updated loan as DTO
     * @throws LoanNotFoundException if loan doesn't exist
     */
    public LoanResponse updateLoan(Long loanId, LoanUpdateRequest request) {
        // Find the loan
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Update only the fields that are provided
        if (request.getActive() != null) {
            loan.setActive(request.getActive());
        }

        if (request.getReturnDate() != null) {
            // In a real app, parse and validate the date
            // For simplicity, we're showing the concept
            // loan.setReturnDate(LocalDate.parse(request.getReturnDate()));
        }

        // IMPORTANT: We're NOT updating book inventory here
        // This is just a field update, not the business operation "return book"
        // For proper return logic, use returnBook() method

        Loan updated = loanRepository.save(loan);
        return loanMapper.toResponse(updated);
    }

    /**
     * Finds a loan by ID.
     *
     * @param loanId ID of the loan
     * @return Loan entity
     * @throws LoanNotFoundException if loan doesn't exist
     */
    //Me ayuda a buscar un prestamos por Id
    public LoanResponse findById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with id " + loanId));
        return loanMapper.toResponse(loan);
    }

    /**
     * Gets all active loans.
     *
     * @return List of active loans as DTOs
     */
    public List<LoanResponse> findActiveLoans() {
        List<Loan> activeLoans = loanRepository.findByActive(true);
        return activeLoans.stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    /**
     * Gets all loans for a specific user.
     *
     * @param userId ID of the user
     * @return List of user's loans as DTOs
     */
    public List<LoanResponse> findLoansByUser(Long userId) {
        // Validate user exists (delegates to UserService)
        // Using findEntityById() - package-private method for internal service-to-service communication
        User user = userService.findEntityById(userId);

        List<Loan> userLoans = loanRepository.findByUser(user);
        return userLoans.stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    /**
     * Gets all loans.
     *
     * @return List of all loans as DTOs
     */
    public List<LoanResponse> findAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    public LoanResponse updatedLoan(long id, Loan updatedLoan) {
        // Buscar el préstamo existente
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + id));

        // Actualizar los campos necesarios
        existingLoan.setActive(updatedLoan.getActive());
        existingLoan.setReturnDate(updatedLoan.getReturnDate());
        existingLoan.setLoanDate(updatedLoan.getLoanDate());
        existingLoan.setBook(updatedLoan.getBook());
        existingLoan.setUser(updatedLoan.getUser());

        // Guardar el préstamo actualizado
        Loan savedLoan = loanRepository.save(existingLoan);

        // Convertir a DTO y devolver
        return loanMapper.toResponse(savedLoan);
    }
}
