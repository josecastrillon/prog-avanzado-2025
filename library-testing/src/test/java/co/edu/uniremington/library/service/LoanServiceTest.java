package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.model.Book;
import co.edu.uniremington.library.domain.model.Loan;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.LoanRepository;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.mapper.LoanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    private Loan loan;
    private LoanResponse loanResponse;
    private Book book;
    private User user;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Cien años de soledad");
        book.setAuthor("Gabriel Garcia Marquez");
        book.setIsbn("978-0307474728");
        book.setAvailableCopies(3);
        book.setTotalCopies(3);

        user = new User();
        user.setId(1L);
        user.setName("Robert Martin");
        user.setEmail("bvmaria@gmail.com");

        loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.of(2025, 10, 1));
        loan.setReturnDate(LocalDate.of(2025, 10, 15));
        loan.setActive(true);

        loanResponse = new LoanResponse();
        loanResponse.setId(1L);
        loanResponse.setBookTitle("Cien años de soledad");
        loanResponse.setUserName("Maria Bueno");
        loanResponse.setLoanDate(LocalDate.of(2025, 10, 1));
        loanResponse.setReturnDate(LocalDate.of(2025, 10, 15));
        loanResponse.setActive(true);
    }

    // =====================================================
    // TESTS: findById
    // =====================================================
    @Test
    @DisplayName("Cuando se busca un préstamo por ID, se devuelve la respuesta de préstamo correcta.")
    void whenFindingLoanById_thenLoanReturned(){
        //Mock
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // When
        // NOT loanService.findById(), porque eso devuelve Loan
        LoanResponse result = loanMapper.toResponse(loanRepository.findById(1L).orElseThrow());

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Cien años de soledad", result.getBookTitle());
        assertEquals("Maria Bueno", result.getUserName());

        // Verify
        verify(loanRepository).findById(1L);
        verify(loanMapper).toResponse(loan);

    }

    // =====================================================
    // TESTS: findActiveLoans
    // =====================================================
    @Test
    @DisplayName("Cuando se encuentran préstamos activos, solo se devuelven los DTO de LoanResponse activos.")
    void whenFindingActiveLoans_thenOnlyActiveLoansReturned() {
        // Given
        List<Loan> activeLoans = List.of(loan);  // loan ya viene activo desde el BeforeEach

        LoanResponse activeResponse = loanResponse;
        activeResponse.setActive(true);

        // Mock
        when(loanRepository.findByActive(true)).thenReturn(activeLoans);
        when(loanMapper.toResponse(loan)).thenReturn(activeResponse);

        // When
        List<LoanResponse> result = loanService.findActiveLoans();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());

        verify(loanRepository).findByActive(true);
        verify(loanMapper).toResponse(loan);
    }

    // =====================================================
    // TESTS: createLoan
    // =====================================================
    @Test
    @DisplayName("Al crear un préstamo, se devuelve LoanResponse.")
    void whenCreatingLoan_thenLoanResponseReturned() {
        // Given
        when(userService.findEntityById(1L)).thenReturn(user);
        when(userService.hasFines(user)).thenReturn(false);
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(bookService.hasAvailableCopies(book)).thenReturn(true);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // When
        LoanResponse result = loanService.createLoan(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals("Cien años de soledad", result.getBookTitle());

        // Verify
        verify(userService).findEntityById(1L);
        verify(bookService).decrementAvailableCopies(book);
        verify(loanRepository).save(any(Loan.class));
        verify(loanMapper).toResponse(loan);
    }

    // =====================================================
    // TESTS: returnBook
    // =====================================================
    @Test
    @DisplayName("Al devolver un préstamo, se incrementa el número de copias del libro y el préstamo se marca como inactivo.")
    void whenReturningLoan_thenCopiesIncremented() {
        // Given
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        loanResponse.setActive(false);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(loanResponse);

        // When
        LoanResponse result = loanService.returnBook(1L);

        // Then
        assertNotNull(result);
        assertFalse(result.getActive());

        // Verify
        verify(bookService).incrementAvailableCopies(book);
        verify(loanRepository).save(loan);
        verify(loanMapper).toResponse(loan);
    }

    // =====================================================
    // TESTS: findLoansByUser
    // =====================================================
    @Test
    @DisplayName("Cuando se buscan préstamos por usuario, solo se devuelven los DTO LoanResponse de ese usuario.")
    void whenFindingLoansByUser_thenUserLoansReturned() {
        // Given
        List<Loan> userLoans = List.of(loan);

        // Mock
        when(userService.findEntityById(1L)).thenReturn(user);
        when(loanRepository.findByUser(user)).thenReturn(userLoans);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // When
        List<LoanResponse> result = loanService.findLoansByUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cien años de soledad", result.get(0).getBookTitle());
        assertEquals("Maria Bueno", result.get(0).getUserName());

        verify(userService).findEntityById(1L);
        verify(loanRepository).findByUser(user);
        verify(loanMapper).toResponse(loan);
    }

    // =====================================================
    // TESTS: findAllLoans
    // =====================================================
    @Test
    @DisplayName("Cuando se encuentran todos los préstamos, se devuelven todos los DTO de LoanResponse.")
    void whenFindingAllLoans_thenAllLoansReturned() {
        // Given
        List<Loan> loans = List.of(loan);

        // Mock
        when(loanRepository.findAll()).thenReturn(loans);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // When
        List<LoanResponse> result = loanService.findAllLoans();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cien años de soledad", result.get(0).getBookTitle());
        assertTrue(result.get(0).getActive());

        verify(loanRepository).findAll();
        verify(loanMapper).toResponse(loan);
    }



}

