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
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * En esta clase hago las pruebas unitarias del servicio LoanService.
 * Aquí verifico que los métodos que manejan los préstamos de libros
 * funcionen correctamente sin errores.
 */
class LoanServiceTest {

    private LoanRepository loanRepository;
    private BookService bookService;
    private UserService userService;
    private LoanMapper loanMapper;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        // Aquí creo versiones simuladas (mock) de las clases que LoanService usa
        loanRepository = mock(LoanRepository.class);
        bookService = mock(BookService.class);
        userService = mock(UserService.class);
        loanMapper = mock(LoanMapper.class);

        // Creo el servicio que voy a probar
        loanService = new LoanService(loanRepository, bookService, userService, loanMapper);
    }

    /**
     * Prueba del método createLoan()
     * Aquí verifico que se pueda crear un préstamo correctamente.
     */
    @Test
    void createLoan() {
        // Datos de ejemplo
        User user = new User();
        Book book = new Book();
        book.setTitle("El principito");

        // Configuro los mocks para simular el comportamiento esperado
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(false);
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(bookService.hasAvailableCopies(book)).thenReturn(true);

        // Simulo el guardado del préstamo
        Loan savedLoan = new Loan();
        savedLoan.setBook(book);
        savedLoan.setUser(user);
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        // Simulo el mapeo a DTO
        LoanResponse response = new LoanResponse();
        when(loanMapper.toResponse(savedLoan)).thenReturn(response);

        // Ejecuto el método
        LoanResponse result = loanService.createLoan(1L, 1L);

        // Verifico resultados
        assertNotNull(result);
        verify(bookService).decrementAvailableCopies(book); // Se debe haber descontado una copia
        verify(loanRepository).save(any(Loan.class)); // Se debió guardar en el repositorio
    }

    /**
     * Prueba del método returnBook()
     * Aquí verifico que se marque un préstamo como devuelto correctamente.
     */
    @Test
    void returnBook() {
        // Preparo un préstamo activo
        Book book = new Book();
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setActive(true);

        // Simulo que el préstamo existe
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(new LoanResponse());

        // Llamo al método
        LoanResponse result = loanService.returnBook(1L);

        // Verifico que haya pasado lo esperado
        assertNotNull(result);
        assertFalse(loan.getActive(), "El préstamo debería quedar inactivo");
        verify(bookService).incrementAvailableCopies(book);
    }

    /**
     * Prueba del método updateLoan()
     * Aquí verifico que se actualice el estado de un préstamo (activo/inactivo).
     */
    @Test
    void updateLoan() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setActive(true);

        LoanUpdateRequest request = new LoanUpdateRequest();
        request.setActive(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(new LoanResponse());

        LoanResponse result = loanService.updateLoan(1L, request);

        assertNotNull(result);
        assertFalse(loan.getActive(), "El préstamo debería haberse marcado como inactivo");
    }

    /**
     * Prueba del método findById()
     * Aquí reviso que se encuentre un préstamo por su ID.
     */
    @Test
    void findById() {
        Loan loan = new Loan();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Loan result = loanService.findById(1L);
        assertNotNull(result);
    }

    /**
     * Prueba del método findActiveLoans()
     * Verifica que se traigan los préstamos activos y se mapeen correctamente.
     */
    @Test
    void findActiveLoans() {
        Loan loan = new Loan();
        loan.setActive(true);

        when(loanRepository.findByActive(true)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(new LoanResponse());

        List<LoanResponse> result = loanService.findActiveLoans();

        assertNotNull(result);
        assertEquals(1, result.size(), "Debe haber un préstamo activo");
    }

    /**
     * Prueba del método findLoansByUser()
     * Aquí verifico que se obtengan todos los préstamos de un usuario específico.
     */
    @Test
    void findLoansByUser() {
        User user = new User();
        when(userService.findEntityById(1L)).thenReturn(user);

        Loan loan = new Loan();
        loan.setUser(user);

        when(loanRepository.findByUser(user)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(new LoanResponse());

        List<LoanResponse> result = loanService.findLoansByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size(), "Debe haber al menos un préstamo del usuario");
    }

    /**
     * Prueba del método findAllLoans()
     * Aquí verifico que se obtengan todos los préstamos del sistema.
     */
    @Test
    void findAllLoans() {
        Loan loan = new Loan();
        when(loanRepository.findAll()).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(new LoanResponse());

        List<LoanResponse> result = loanService.findAllLoans();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
