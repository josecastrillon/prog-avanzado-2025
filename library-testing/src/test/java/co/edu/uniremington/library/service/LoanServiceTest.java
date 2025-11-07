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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private User user;
    private Book book;
    private Loan loan;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Juan");

        book = new Book();
        book.setId(10L);
        book.setTitle("El Principito");

        loan = new Loan();
        loan.setId(100L);
        loan.setUser(user);
        loan.setBook(book);
        loan.setActive(true);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(14));

        loanResponse = new LoanResponse();
        loanResponse.setId(100L);
        loanResponse.setBookTitle("El Principito");
        loanResponse.setUserName("Juan");
        loanResponse.setActive(true);
    }

    // ------------------------------------------------------------
    // 1️⃣ createLoan()
    // ------------------------------------------------------------
    @Test
    void createLoan_success() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(false);
        when(bookService.findEntityById(10L)).thenReturn(book);
        when(bookService.hasAvailableCopies(book)).thenReturn(true);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.createLoan(10L, 1L);

        assertNotNull(result);
        assertEquals("El Principito", result.getBookTitle());
        verify(bookService).decrementAvailableCopies(book);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoan_userWithFines_throwsException() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(true);

        assertThrows(UserWithFinesException.class, () -> loanService.createLoan(10L, 1L));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void createLoan_noAvailableCopies_throwsException() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(false);
        when(bookService.findEntityById(10L)).thenReturn(book);
        when(bookService.hasAvailableCopies(book)).thenReturn(false);

        assertThrows(NoAvailableCopiesException.class, () -> loanService.createLoan(10L, 1L));
        verify(loanRepository, never()).save(any());
    }

    // ------------------------------------------------------------
    // 2️⃣ returnBook()
    // ------------------------------------------------------------
    @Test
    void returnBook_success() {
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.returnBook(100L);

        assertNotNull(result);
        verify(bookService).incrementAvailableCopies(book);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void returnBook_loanAlreadyReturned_throwsException() {
        loan.setActive(false);
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));

        assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnBook(100L));
        verify(bookService, never()).incrementAvailableCopies(any());
    }

    @Test
    void returnBook_notFound_throwsException() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(LoanNotFoundException.class, () -> loanService.returnBook(999L));
    }

    // ------------------------------------------------------------
    // 3️⃣ updateLoan()
    // ------------------------------------------------------------
    @Test
    void updateLoan_success() {
        LoanUpdateRequest request = new LoanUpdateRequest();
        request.setActive(false);

        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.updateLoan(100L, request);

        assertNotNull(result);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void updateLoan_notFound_throwsException() {
        when(loanRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(LoanNotFoundException.class, () -> loanService.updateLoan(100L, new LoanUpdateRequest()));
    }

    // ------------------------------------------------------------
    // 4️⃣ findById()
    // ------------------------------------------------------------
    @Test
    void findById_success() {
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));

        Loan result = loanService.findById(100L);

        assertNotNull(result);
        assertEquals(loan, result);
    }

    @Test
    void findById_notFound_throwsException() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(LoanNotFoundException.class, () -> loanService.findById(999L));
    }

    // ------------------------------------------------------------
    // 5️⃣ findActiveLoans()
    // ------------------------------------------------------------
    @Test
    void findActiveLoans_success() {
        when(loanRepository.findByActive(true)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findActiveLoans();

        assertEquals(1, result.size());
        assertEquals("El Principito", result.get(0).getBookTitle());
        verify(loanRepository).findByActive(true);
    }

    // ------------------------------------------------------------
    // 6️⃣ findLoansByUser()
    // ------------------------------------------------------------
    @Test
    void findLoansByUser_success() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(loanRepository.findByUser(user)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findLoansByUser(1L);

        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getUserName());
        verify(loanRepository).findByUser(user);
    }

    // ------------------------------------------------------------
    // 7️⃣ findAllLoans()
    // ------------------------------------------------------------
    @Test
    void findAllLoans_success() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findAllLoans();

        assertEquals(1, result.size());
        verify(loanRepository).findAll();
    }
}
