package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.*;
import co.edu.uniremington.library.domain.model.*;
import co.edu.uniremington.library.repository.LoanRepository;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.dto.LoanUpdateRequest;
import co.edu.uniremington.library.service.mapper.LoanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
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

    private Book book;
    private User user;
    private Loan loan;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {

        // 📘 Crear un libro de ejemplo
        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");

        // 👤 Crear un usuario de ejemplo
        user = new User();
        user.setId(1L);

        // 📚 Crear un préstamo de ejemplo (Loan)
        loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(14));
        loan.setActive(true);

        // 🧾 Crear un DTO de respuesta vacío (simulado)
        loanResponse = new LoanResponse();
    }



    @Test
    void createLoan_success() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(false);
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(bookService.hasAvailableCopies(book)).thenReturn(true);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse response = loanService.createLoan(1L, 1L);

        assertNotNull(response);
        verify(bookService).decrementAvailableCopies(book);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoan_userHasFines_throwsException() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(true);

        assertThrows(UserWithFinesException.class, () -> loanService.createLoan(1L, 1L));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void createLoan_noAvailableCopies_throwsException() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(false);
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(bookService.hasAvailableCopies(book)).thenReturn(false);

        assertThrows(NoAvailableCopiesException.class, () -> loanService.createLoan(1L, 1L));
    }

    @Test
    void returnBook_success() {
        loan.setActive(true);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.returnBook(1L);

        assertNotNull(result);
        verify(bookService).incrementAvailableCopies(book);
        verify(loanRepository).save(loan);
    }

    @Test
    void returnBook_alreadyReturned_throwsException() {
        loan.setActive(false);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnBook(1L));
    }

    @Test
    void updateLoan_success() {
        LoanUpdateRequest request = new LoanUpdateRequest();
        request.setActive(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        LoanResponse result = loanService.updateLoan(1L, request);

        assertNotNull(result);
        assertFalse(loan.getActive());
    }

    @Test
    void findById_success() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Loan result = loanService.findById(1L);

        assertEquals(loan, result);
    }

    @Test
    void findById_notFound_throwsException() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.findById(1L));
    }

    @Test
    void findActiveLoans_success() {
        when(loanRepository.findByActive(true)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        List<LoanResponse> results = loanService.findActiveLoans();

        assertEquals(1, results.size());
        verify(loanRepository).findByActive(true);
    }

    @Test
    void findLoansByUser_success() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(loanRepository.findByUser(user)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        List<LoanResponse> results = loanService.findLoansByUser(1L);

        assertEquals(1, results.size());
        verify(loanRepository).findByUser(user);
    }

    @Test
    void findAllLoans_success() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        List<LoanResponse> results = loanService.findAllLoans();

        assertEquals(1, results.size());
        verify(loanRepository).findAll();
    }
}