package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.model.*;
import co.edu.uniremington.library.repository.LoanRepository;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.dto.LoanUpdateRequest;
import co.edu.uniremington.library.service.mapper.LoanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

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

        // Crear objetos simulados para pruebas
        user = new User();
        user.setId(1L);
        user.setHasFines(false);

        book = new Book();
        book.setId(1L);
        book.setTitle("El Principito");

        loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setActive(true);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(14));

        loanResponse = new LoanResponse();
        loanResponse.setId(1L);
    }

    @Test
    void createLoan() {
        // Configurar mocks
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(false);
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(bookService.hasAvailableCopies(book)).thenReturn(true);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        // Ejecutar método
        LoanResponse result = loanService.createLoan(1L, 1L);

        // Validar resultados
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookService).decrementAvailableCopies(book);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void returnBook() {
        // Simular préstamo activo
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // Ejecutar método
        LoanResponse result = loanService.returnBook(1L);

        // Validar resultados
        assertNotNull(result);
        assertFalse(loan.getActive());
        verify(bookService).incrementAvailableCopies(book);
        verify(loanRepository).save(loan);
    }

    @Test
    void updateLoan() {
        // Crear solicitud de actualización
        LoanUpdateRequest req = new LoanUpdateRequest();
        req.setActive(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // Ejecutar método
        LoanResponse result = loanService.updateLoan(1L, req);

        // Validar resultados
        assertNotNull(result);
        assertFalse(loan.getActive());
        verify(loanRepository).save(loan);
    }

    @Test
    void findById() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Loan result = loanService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(loanRepository).findById(1L);
    }

    @Test
    void findActiveLoans() {
        when(loanRepository.findByActive(true)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findActiveLoans();

        assertEquals(1, result.size());
        verify(loanRepository).findByActive(true);
    }

    @Test
    void findLoansByUser() {
        when(userService.findEntityById(1L)).thenReturn(user);
        when(loanRepository.findByUser(user)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findLoansByUser(1L);

        assertEquals(1, result.size());
        verify(loanRepository).findByUser(user);
    }

    @Test
    void findAllLoans() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        List<LoanResponse> result = loanService.findAllLoans();

        assertEquals(1, result.size());
        verify(loanRepository).findAll();
    }
}
