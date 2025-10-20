package co.edu.uniremington.library.service.mapper;

import co.edu.uniremington.library.domain.model.Book;
import co.edu.uniremington.library.domain.model.Loan;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.service.dto.LoanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoanMapper.
 *
 * TESTING MAPPERS:
 * ================
 * Mappers are SIMPLE components that transform data between entities and DTOs.
 * They typically don't have external dependencies, so we test them WITHOUT mocks.
 *
 * WHY TEST MAPPERS?
 * =================
 * 1. Verify all fields are correctly mapped
 * 2. Ensure nested properties (book.title, user.name) are extracted properly
 * 3. Confirm null handling is correct
 * 4. Document the transformation logic through tests
 *
 * NOTICE: No @ExtendWith(MockitoExtension.class) needed!
 * ========================================================
 * Mapper tests are pure unit tests - create real objects, verify transformations.
 *
 * BENEFITS OF TESTING MAPPERS SEPARATELY:
 * ========================================
 * ✅ Isolates mapping logic from business logic
 * ✅ Easy to verify all fields are mapped correctly
 * ✅ Fast tests (no mocking overhead)
 * ✅ Clear documentation of DTO structure
 * ✅ Catches mapping bugs early
 *
 * LEARNING OBJECTIVES:
 * ====================
 * - Understand the difference between testing with and without mocks
 * - Learn to verify object transformations
 * - Practice testing edge cases (null values)
 * - Understand how to test list transformations
 */
class LoanMapperTest {

    private LoanMapper loanMapper;
    private Book book;
    private User user;
    private Loan loan;

    @BeforeEach
    void setUp() {
        // Create a real instance of the mapper (no mocking needed)
        loanMapper = new LoanMapper();

        // Create test data
        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setIsbn("978-0132350884");
        book.setAvailableCopies(5);
        book.setTotalCopies(5);

        user = new User();
        user.setId(1L);
        user.setName("Juan Perez");
        user.setEmail("juan@email.com");
        user.setHasFines(false);

        loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.of(2024, 1, 15));
        loan.setReturnDate(LocalDate.of(2024, 1, 29));
        loan.setActive(true);
    }

    @Test
    @DisplayName("WHEN mapping valid Loan THEN all fields correctly mapped to LoanResponse")
    void whenMappingValidLoan_thenAllFieldsCorrectlyMapped() {
        // When
        LoanResponse result = loanMapper.toResponse(loan);

        // Then - Verify every single field
        assertNotNull(result, "LoanResponse should not be null");
        assertEquals(1L, result.getId(), "ID should be mapped correctly");
        assertEquals("Clean Code", result.getBookTitle(), "Book title should be extracted from loan.book.title");
        assertEquals("Juan Perez", result.getUserName(), "User name should be extracted from loan.user.name");
        assertEquals(LocalDate.of(2024, 1, 15), result.getLoanDate(), "Loan date should be mapped correctly");
        assertEquals(LocalDate.of(2024, 1, 29), result.getReturnDate(), "Return date should be mapped correctly");
        assertTrue(result.getActive(), "Active status should be mapped correctly");
    }

    @Test
    @DisplayName("WHEN mapping null Loan THEN returns null (defensive programming)")
    void whenMappingNullLoan_thenReturnsNull() {
        // When
        LoanResponse result = loanMapper.toResponse(null);

        // Then
        assertNull(result, "Mapping null should return null (not throw exception)");
    }

    @Test
    @DisplayName("WHEN mapping inactive Loan THEN active field is false")
    void whenMappingInactiveLoan_thenActiveFieldIsFalse() {
        // Given
        loan.setActive(false);

        // When
        LoanResponse result = loanMapper.toResponse(loan);

        // Then
        assertNotNull(result);
        assertFalse(result.getActive(), "Inactive loan should have active=false in DTO");
    }

    @Test
    @DisplayName("WHEN mapping Loan with different book THEN book title correctly extracted")
    void whenMappingLoanWithDifferentBook_thenBookTitleCorrectlyExtracted() {
        // Given
        Book differentBook = new Book();
        differentBook.setId(2L);
        differentBook.setTitle("Refactoring");
        differentBook.setAuthor("Martin Fowler");
        differentBook.setIsbn("978-0201485677");
        loan.setBook(differentBook);

        // When
        LoanResponse result = loanMapper.toResponse(loan);

        // Then
        assertEquals("Refactoring", result.getBookTitle(),
                "Should map the correct book title from nested book object");
    }

    @Test
    @DisplayName("WHEN mapping Loan with different user THEN user name correctly extracted")
    void whenMappingLoanWithDifferentUser_thenUserNameCorrectlyExtracted() {
        // Given
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setName("Maria Lopez");
        differentUser.setEmail("maria@email.com");
        loan.setUser(differentUser);

        // When
        LoanResponse result = loanMapper.toResponse(loan);

        // Then
        assertEquals("Maria Lopez", result.getUserName(),
                "Should map the correct user name from nested user object");
    }

    @Test
    @DisplayName("WHEN mapping list of Loans THEN all loans correctly mapped")
    void whenMappingListOfLoans_thenAllLoansCorrectlyMapped() {
        // Given
        Loan loan2 = new Loan();
        loan2.setId(2L);
        loan2.setBook(book);
        loan2.setUser(user);
        loan2.setLoanDate(LocalDate.of(2024, 2, 1));
        loan2.setReturnDate(LocalDate.of(2024, 2, 15));
        loan2.setActive(true);

        List<Loan> loans = Arrays.asList(loan, loan2);

        // When
        List<LoanResponse> results = loanMapper.toResponseList(loans);

        // Then
        assertNotNull(results, "Result list should not be null");
        assertEquals(2, results.size(), "Should map all loans in the list");

        // Verify first loan
        LoanResponse first = results.get(0);
        assertEquals(1L, first.getId());
        assertEquals("Clean Code", first.getBookTitle());

        // Verify second loan
        LoanResponse second = results.get(1);
        assertEquals(2L, second.getId());
        assertEquals(LocalDate.of(2024, 2, 1), second.getLoanDate());
    }

    @Test
    @DisplayName("WHEN mapping null list THEN returns null")
    void whenMappingNullList_thenReturnsNull() {
        // When
        List<LoanResponse> result = loanMapper.toResponseList(null);

        // Then
        assertNull(result, "Mapping null list should return null (not throw exception)");
    }

    @Test
    @DisplayName("WHEN mapping empty list THEN returns empty list")
    void whenMappingEmptyList_thenReturnsEmptyList() {
        // Given
        List<Loan> emptyList = Arrays.asList();

        // When
        List<LoanResponse> result = loanMapper.toResponseList(emptyList);

        // Then
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Empty list should map to empty list");
    }
}

/*
 * EXERCISE FOR STUDENTS:
 * ======================
 * 1. Add a test for mapping a loan with null dates
 * 2. Create BookMapper and BookMapperTest following this pattern
 * 3. Test what happens if loan.getBook() is null (should it throw NPE or handle gracefully?)
 * 4. Add a test verifying that changing the entity after mapping doesn't affect the DTO
 *    (proves DTO is a true independent copy)
 * 5. Compare this test structure with LibraryServiceTest - notice the differences!
 */
