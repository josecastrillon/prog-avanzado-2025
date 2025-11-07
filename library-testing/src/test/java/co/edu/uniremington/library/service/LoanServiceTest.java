package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.*;
import co.edu.uniremington.library.domain.model.Book;
import co.edu.uniremington.library.domain.model.Loan;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.LoanRepository;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.dto.LoanUpdateRequest;
import co.edu.uniremington.library.service.mapper.LoanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoanService.
 *
 * Following the same structure and style as BookServiceTest.
 */
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    private Loan loan;
    private LoanResponse loanResponse;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Leidy");

        book = new Book();
        book.setId(10L);
        book.setTitle("Amigurumis Creativos");

        loan = new Loan();
        loan.setId(1L);
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(14));
        loan.setActive(true);

        loanResponse = new LoanResponse();
        loanResponse.setId(1L);
        loanResponse.setLoanDate(loan.getLoanDate());
        loanResponse.setReturnDate(loan.getReturnDate());
    }

    // =====================================================
    // TEST: createLoan
    // =====================================================
    @Test
    @DisplayName("WHEN creating a loan successfully THEN return LoanResponse DTO")
    void whenCreatingLoanSuccessfully_thenReturnLoanResponse() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(false);
        when(bookService.findEntityById(10L)).thenReturn(book);
        when(bookService.hasAvailableCopies(book)).thenReturn(true);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        LoanResponse result = loanService.createLoan(10L, 1L);

        assertNotNull(result);
        assertEquals(loanResponse.getId(), result.getId());
        verify(userService).findEntityById(1L);
        verify(bookService).findEntityById(10L);
        verify(bookService).decrementAvailableCopies(book);
        verify(loanRepository).save(any(Loan.class));
        verify(loanMapper).toResponse(loan);
    }

    @Test
    @DisplayName("WHEN user has fines THEN throw UserWithFinesException")
    void whenUserHasFines_thenThrowException() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(true);

        assertThrows(UserWithFinesException.class, () -> loanService.createLoan(10L, 1L));

        verify(userService).findEntityById(1L);
        verify(userService).hasFines(user);
        verify(bookService, never()).findEntityById(any());
        verify(loanRepository, never()).save(any());
    }

    // =====================================================
    // TEST: returnBook
    // =====================================================
    @Test
    @DisplayName("WHEN returning an active loan THEN loan is marked inactive")
    void whenReturningActiveLoan_thenLoanIsMarkedInactive() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        LoanResponse result = loanService.returnBook(1L);

        assertNotNull(result);
        assertFalse(loan.getActive());
        verify(bookService).incrementAvailableCopies(book);
        verify(loanRepository).save(loan);
        verify(loanMapper).toResponse(loan);
    }

    @Test
    @DisplayName("WHEN returning non-existent loan THEN throw LoanNotFoundException")
    void whenReturningNonExistentLoan_thenThrowException() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.returnBook(99L));

        verify(loanRepository).findById(99L);
    }

    @Test
    @DisplayName("WHEN returning already returned loan THEN throw LoanAlreadyReturnedException")
    void whenReturningAlreadyReturnedLoan_thenThrowException() {
        loan.setActive(false);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnBook(1L));

        verify(loanRepository).findById(1L);
        verify(bookService, never()).incrementAvailableCopies(any());
    }

    // =====================================================
    // TEST: updateLoan
    // =====================================================
    @Test
    @DisplayName("WHEN updating loan THEN return updated LoanResponse")
    void whenUpdatingLoan_thenReturnUpdatedResponse() {
        LoanUpdateRequest request = new LoanUpdateRequest();
        request.setActive(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        LoanResponse result = loanService.updateLoan(1L, request);

        assertNotNull(result);
        verify(loanRepository).findById(1L);
        verify(loanRepository).save(loan);
        verify(loanMapper).toResponse(loan);
    }

    @Test
    @DisplayName("WHEN updating non-existent loan THEN throw LoanNotFoundException")
    void whenUpdatingNonExistentLoan_thenThrowException() {
        LoanUpdateRequest request = new LoanUpdateRequest();
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.updateLoan(99L, request));

        verify(loanRepository).findById(99L);
        verify(loanRepository, never()).save(any());
    }

    // =====================================================
    // TEST: findById
    // =====================================================
    @Test
    @DisplayName("WHEN finding loan by ID THEN return Loan entity")
    void whenFindingLoanById_thenReturnLoan() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Loan result = loanService.findById(1L);

        assertNotNull(result);
        assertEquals(loan.getId(), result.getId());
        verify(loanRepository).findById(1L);
    }

    @Test
    @DisplayName("WHEN loan not found by ID THEN throw LoanNotFoundException")
    void whenLoanNotFoundById_thenThrowException() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.findById(99L));

        verify(loanRepository).findById(99L);
    }

    // =====================================================
    // TEST: findActiveLoans
    // =====================================================
    @Test
    @DisplayName("WHEN finding active loans THEN return list of LoanResponses")
    void whenFindingActiveLoans_thenReturnList() {
        when(loanRepository.findByActive(true)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findActiveLoans();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepository).findByActive(true);
        verify(loanMapper).toResponse(loan);
    }

    // =====================================================
    // TEST: findLoansByUser
    // =====================================================
    @Test
    @DisplayName("WHEN finding loans by user THEN return list of LoanResponses")
    void whenFindingLoansByUser_thenReturnList() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(loanRepository.findByUser(user)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findLoansByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userService).findEntityById(1L);
        verify(loanRepository).findByUser(user);
        verify(loanMapper).toResponse(loan);
    }

    // =====================================================
    // TEST: findAllLoans
    // =====================================================
    @Test
    @DisplayName("WHEN finding all loans THEN return list of LoanResponses")
    void whenFindingAllLoans_thenReturnList() {
        Loan anotherLoan = new Loan();
        anotherLoan.setId(2L);
        anotherLoan.setUser(user);
        anotherLoan.setBook(book);
        anotherLoan.setActive(false);

        when(loanRepository.findAll()).thenReturn(Arrays.asList(loan, anotherLoan));
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findAllLoans();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(loanRepository).findAll();
        verify(loanMapper, times(2)).toResponse(any(Loan.class));
    }
}
