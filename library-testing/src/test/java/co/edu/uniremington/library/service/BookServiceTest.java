package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.BookNotFoundException;
import co.edu.uniremington.library.domain.model.Book;
import co.edu.uniremington.library.repository.BookRepository;
import co.edu.uniremington.library.service.dto.BookRequest;
import co.edu.uniremington.library.service.dto.BookResponse;
import co.edu.uniremington.library.service.mapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookService.

 *
 * MOCKITO BASICS :
 * =============================
 * @Mock - Creates fake dependencies (Repository + Mapper)
 * @InjectMocks - Creates BookService and injects mocks
 * when().thenReturn() - Configure mock behavior
 * verify() - Verify methods were called
 * assertThrows() - Verify exceptions are thrown
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;  // ← NUEVO: Mock del mapper

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        // Setup entity (used internally by service)
        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setIsbn("978-0132350884");
        book.setAvailableCopies(5);
        book.setTotalCopies(5);

        // Setup DTO (returned by public API methods)
        bookResponse = new BookResponse();
        bookResponse.setId(1L);
        bookResponse.setTitle("Clean Code");
        bookResponse.setAuthor("Robert Martin");
        bookResponse.setIsbn("978-0132350884");
        bookResponse.setAvailableCopies(5);
        bookResponse.setTotalCopies(5);
    }

    // =====================================================
    // TESTS: findById
    // =====================================================

    @Test
    @DisplayName("WHEN finding book by existing ID THEN BookResponse DTO is returned")
    void whenFindingBookByExistingId_thenBookResponseIsReturned() {
        // Given - Mock repository to return entity and mapper to convert to DTO
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        // When - Call the service method
        BookResponse result = bookService.findById(1L);

        // Then - Verify the DTO result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Clean Code", result.getTitle());
        assertEquals("Robert Martin", result.getAuthor());

        // Verify the complete flow: repository → entity → mapper → DTO
        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, times(1)).toResponse(book);
    }

    @Test
    @DisplayName("WHEN finding book by non-existent ID THEN throw BookNotFoundException")
    void whenFindingBookByNonExistentId_thenThrowException() {
        // Given - Configure mock to return empty Optional
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then - Verify exception is thrown
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.findById(99L);
        });

        // Verify exception message
        assertTrue(exception.getMessage().contains("Book not found with ID: 99"));

        // Verify repository was called but mapper was NOT (exception thrown first)
        verify(bookRepository).findById(99L);
        verify(bookMapper, never()).toResponse(any());
    }

    // =====================================================
    // TESTS: findAvailableBooks
    // =====================================================

    @Test
    @DisplayName("WHEN finding available books THEN only BookResponse DTOs with copies > 0 are returned")
    void whenFindingAvailableBooks_thenOnlyAvailableBooksReturned() {
        // Given - Create test entities
        Book availableBook1 = new Book();
        availableBook1.setId(1L);
        availableBook1.setTitle("Clean Code");
        availableBook1.setAvailableCopies(5);

        Book availableBook2 = new Book();
        availableBook2.setId(2L);
        availableBook2.setTitle("Refactoring");
        availableBook2.setAvailableCopies(2);

        List<Book> availableBooks = Arrays.asList(availableBook1, availableBook2);

        // Create corresponding DTOs
        BookResponse response1 = new BookResponse();
        response1.setId(1L);
        response1.setTitle("Clean Code");
        response1.setAvailableCopies(5);

        BookResponse response2 = new BookResponse();
        response2.setId(2L);
        response2.setTitle("Refactoring");
        response2.setAvailableCopies(2);

        List<BookResponse> responses = Arrays.asList(response1, response2);

        // Mock repository and mapper
        when(bookRepository.findByAvailableCopiesGreaterThan(0)).thenReturn(availableBooks);
        when(bookMapper.toResponseList(availableBooks)).thenReturn(responses);

        // When
        List<BookResponse> result = bookService.findAvailableBooks();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getAvailableCopies() > 0));

        // Verify complete flow
        verify(bookRepository).findByAvailableCopiesGreaterThan(0);
        verify(bookMapper).toResponseList(availableBooks);
    }

    @Test
    @DisplayName("WHEN no available books THEN return empty list")
    void whenNoAvailableBooks_thenReturnEmptyList() {
        // Given
        when(bookRepository.findByAvailableCopiesGreaterThan(0)).thenReturn(Arrays.asList());
        when(bookMapper.toResponseList(Arrays.asList())).thenReturn(Arrays.asList());

        // When
        List<BookResponse> result = bookService.findAvailableBooks();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepository).findByAvailableCopiesGreaterThan(0);
        verify(bookMapper).toResponseList(Arrays.asList());
    }

    // =====================================================
    // TESTS: findAllBooks
    // =====================================================

    @Test
    @DisplayName("WHEN finding all books THEN all BookResponse DTOs are returned regardless of availability")
    void whenFindingAllBooks_thenAllBooksReturned() {
        // Given - Create entities
        Book unavailableBook = new Book();
        unavailableBook.setId(3L);
        unavailableBook.setTitle("Sold Out Book");
        unavailableBook.setAvailableCopies(0);
        unavailableBook.setTotalCopies(5);

        List<Book> allBooks = Arrays.asList(book, unavailableBook);

        // Create corresponding DTOs
        BookResponse unavailableResponse = new BookResponse();
        unavailableResponse.setId(3L);
        unavailableResponse.setTitle("Sold Out Book");
        unavailableResponse.setAvailableCopies(0);
        unavailableResponse.setTotalCopies(5);

        List<BookResponse> allResponses = Arrays.asList(bookResponse, unavailableResponse);

        // Mock repository and mapper
        when(bookRepository.findAll()).thenReturn(allBooks);
        when(bookMapper.toResponseList(allBooks)).thenReturn(allResponses);

        // When
        List<BookResponse> result = bookService.findAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        // Verify we get both available and unavailable books
        assertTrue(result.stream().anyMatch(b -> b.getAvailableCopies() == 0));
        assertTrue(result.stream().anyMatch(b -> b.getAvailableCopies() > 0));

        verify(bookRepository).findAll();
        verify(bookMapper).toResponseList(allBooks);
    }

    // =====================================================
    // TESTS: registerNewBook
    // =====================================================

    @Test
    @DisplayName("WHEN registering new book with all fields THEN BookResponse is returned")
    void whenRegisteringNewBookWithAllFields_thenBookResponseReturned() {
        // Given - Create request DTO
        BookRequest request = new BookRequest();
        request.setTitle("New Book");
        request.setAuthor("Author Name");
        request.setIsbn("123-456");
        request.setTotalCopies(10);

        // Create entity from request
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("Author Name");
        newBook.setIsbn("123-456");
        newBook.setTotalCopies(10);
        newBook.setAvailableCopies(10);

        // Create saved entity
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("New Book");
        savedBook.setAuthor("Author Name");
        savedBook.setIsbn("123-456");
        savedBook.setTotalCopies(10);
        savedBook.setAvailableCopies(10);

        // Create response DTO
        BookResponse response = new BookResponse();
        response.setId(1L);
        response.setTitle("New Book");
        response.setAuthor("Author Name");
        response.setIsbn("123-456");
        response.setTotalCopies(10);
        response.setAvailableCopies(10);

        // Mock the complete flow: Request → Entity → Save → Response
        when(bookMapper.toEntity(request)).thenReturn(newBook);
        when(bookRepository.save(newBook)).thenReturn(savedBook);
        when(bookMapper.toResponse(savedBook)).thenReturn(response);

        // When
        BookResponse result = bookService.registerNewBook(request);

        // Then
        assertNotNull(result);
        assertEquals(10, result.getTotalCopies());
        assertEquals(10, result.getAvailableCopies());

        // Verify complete flow
        verify(bookMapper).toEntity(request);
        verify(bookRepository).save(newBook);
        verify(bookMapper).toResponse(savedBook);
    }

    @Test
    @DisplayName("WHEN registering book without totalCopies THEN default to 1")
    void whenRegisteringBookWithoutTotalCopies_thenDefaultToOne() {
        // Given - Request without totalCopies
        BookRequest request = new BookRequest();
        request.setTitle("New Book");
        request.setAuthor("Author Name");
        request.setIsbn("123-456");
        // totalCopies is NOT set

        // Entity from mapper
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("Author Name");
        newBook.setIsbn("123-456");
        newBook.setTotalCopies(null);  // NULL - service will apply default

        // Mock mapper and repository (repository returns same book that was passed in)
        when(bookMapper.toEntity(request)).thenReturn(newBook);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookMapper.toResponse(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            BookResponse response = new BookResponse();
            response.setTitle(savedBook.getTitle());
            response.setTotalCopies(savedBook.getTotalCopies());
            response.setAvailableCopies(savedBook.getAvailableCopies());
            return response;
        });

        // When
        BookResponse result = bookService.registerNewBook(request);

        // Then - Service applied defaults
        assertNotNull(result);
        assertEquals(1, result.getTotalCopies(), "Should default to 1 when null");
        assertEquals(1, result.getAvailableCopies(), "Should match totalCopies");

        verify(bookMapper).toEntity(request);
        verify(bookRepository).save(newBook);
    }

    @Test
    @DisplayName("WHEN registering book without availableCopies THEN default to totalCopies")
    void whenRegisteringBookWithoutAvailableCopies_thenDefaultToTotalCopies() {
        // Given - Request with totalCopies but not availableCopies
        BookRequest request = new BookRequest();
        request.setTitle("New Book");
        request.setAuthor("Author Name");
        request.setIsbn("123-456");
        request.setTotalCopies(7);

        // Entity from mapper
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("Author Name");
        newBook.setIsbn("123-456");
        newBook.setTotalCopies(7);
        newBook.setAvailableCopies(null);  // NULL - service will apply default

        when(bookMapper.toEntity(request)).thenReturn(newBook);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookMapper.toResponse(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            BookResponse response = new BookResponse();
            response.setTitle(savedBook.getTitle());
            response.setTotalCopies(savedBook.getTotalCopies());
            response.setAvailableCopies(savedBook.getAvailableCopies());
            return response;
        });

        // When
        BookResponse result = bookService.registerNewBook(request);

        // Then
        assertNotNull(result);
        assertEquals(7, result.getTotalCopies());
        assertEquals(7, result.getAvailableCopies(), "Should default to totalCopies");

        verify(bookMapper).toEntity(request);
        verify(bookRepository).save(newBook);
    }

    @Test
    @DisplayName("WHEN registering book with NO copies specified THEN default both to 1")
    void whenRegisteringBookWithNoCopiesSpecified_thenDefaultBothToOne() {
        // Given - Request without any copies
        BookRequest request = new BookRequest();
        request.setTitle("New Book");
        request.setAuthor("Author Name");
        request.setIsbn("123-456");

        // Entity from mapper - no copies set
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("Author Name");
        newBook.setIsbn("123-456");
        newBook.setTotalCopies(null);
        newBook.setAvailableCopies(null);

        when(bookMapper.toEntity(request)).thenReturn(newBook);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookMapper.toResponse(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            BookResponse response = new BookResponse();
            response.setTitle(savedBook.getTitle());
            response.setTotalCopies(savedBook.getTotalCopies());
            response.setAvailableCopies(savedBook.getAvailableCopies());
            return response;
        });

        // When
        BookResponse result = bookService.registerNewBook(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalCopies(), "Should default totalCopies to 1");
        assertEquals(1, result.getAvailableCopies(), "Should default availableCopies to 1");

        verify(bookMapper).toEntity(request);
        verify(bookRepository).save(newBook);
    }

    // =====================================================
    // TESTS: hasAvailableCopies
    // =====================================================

    @Test
    @DisplayName("WHEN book has available copies THEN return true")
    void whenBookHasAvailableCopies_thenReturnTrue() {
        // Given
        book.setAvailableCopies(3);

        // When
        boolean result = bookService.hasAvailableCopies(book);

        // Then
        assertTrue(result, "Should return true when availableCopies > 0");
    }

    @Test
    @DisplayName("WHEN book has no available copies THEN return false")
    void whenBookHasNoAvailableCopies_thenReturnFalse() {
        // Given
        book.setAvailableCopies(0);

        // When
        boolean result = bookService.hasAvailableCopies(book);

        // Then
        assertFalse(result, "Should return false when availableCopies = 0");
    }

    // =====================================================
    // TESTS: decrementAvailableCopies (package-private)
    // =====================================================

    @Test
    @DisplayName("WHEN decrementing available copies THEN inventory is reduced by 1")
    void whenDecrementingAvailableCopies_thenInventoryReducedByOne() {
        // Given
        book.setAvailableCopies(5);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        bookService.decrementAvailableCopies(book);

        // Then
        assertEquals(4, book.getAvailableCopies(), "Available copies should decrease by 1");

        // Verify save was called
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("WHEN decrementing from 1 copy THEN goes to 0")
    void whenDecrementingFromOneCopy_thenGoesToZero() {
        // Given
        book.setAvailableCopies(1);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        bookService.decrementAvailableCopies(book);

        // Then
        assertEquals(0, book.getAvailableCopies(), "Should go to 0 when decrementing from 1");

        verify(bookRepository).save(book);
    }

    // =====================================================
    // TESTS: incrementAvailableCopies (package-private)
    // =====================================================

    @Test
    @DisplayName("WHEN incrementing available copies THEN inventory is increased by 1")
    void whenIncrementingAvailableCopies_thenInventoryIncreasedByOne() {
        // Given
        book.setAvailableCopies(3);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        bookService.incrementAvailableCopies(book);

        // Then
        assertEquals(4, book.getAvailableCopies(), "Available copies should increase by 1");

        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("WHEN incrementing from 0 copies THEN goes to 1")
    void whenIncrementingFromZeroCopies_thenGoesToOne() {
        // Given
        book.setAvailableCopies(0);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        bookService.incrementAvailableCopies(book);

        // Then
        assertEquals(1, book.getAvailableCopies(), "Should go to 1 when incrementing from 0");

        verify(bookRepository).save(book);
    }
}

