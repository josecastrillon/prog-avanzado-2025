package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.domain.exception.BookNotFoundException;
import co.edu.uniremington.library.service.BookService;
import co.edu.uniremington.library.service.dto.BookRequest;
import co.edu.uniremington.library.service.dto.BookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for BookController.
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;  // ← ONLY mock service, NO mapper!

    @Autowired
    private ObjectMapper objectMapper;

    private BookResponse response1;
    private BookResponse response2;

    @BeforeEach
    void setUp() {
        // Setup DTOs - Service now returns DTOs directly!
        // No need for Book entities in controller tests
        response1 = new BookResponse();
        response1.setId(1L);
        response1.setTitle("Clean Code");
        response1.setAuthor("Robert Martin");
        response1.setIsbn("978-0132350884");
        response1.setAvailableCopies(5);
        response1.setTotalCopies(5);

        response2 = new BookResponse();
        response2.setId(2L);
        response2.setTitle("Refactoring");
        response2.setAuthor("Martin Fowler");
        response2.setIsbn("978-0201485677");
        response2.setAvailableCopies(0);
        response2.setTotalCopies(3);
    }

    // =====================================================
    // TESTS: GET /api/books
    // =====================================================

    @Test
    @DisplayName("GET /api/books - WHEN getting all books THEN return HTTP 200 with list")
    void whenGettingAllBooks_thenReturnHttp200WithList() throws Exception {
        // Given - Service returns DTOs directly (no mapper needed!)
        List<BookResponse> allResponses = Arrays.asList(response1, response2);
        when(bookService.findAllBooks()).thenReturn(allResponses);

        // When & Then - Controller returns DTOs directly from service
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Clean Code")))
                .andExpect(jsonPath("$[0].author", is("Robert Martin")))
                .andExpect(jsonPath("$[1].title", is("Refactoring")))
                .andExpect(jsonPath("$[1].availableCopies", is(0)));

        verify(bookService, times(1)).findAllBooks();
    }

    @Test
    @DisplayName("GET /api/books - WHEN no books exist THEN return HTTP 200 with empty list")
    void whenNoBooks_thenReturnHttp200WithEmptyList() throws Exception {
        // Given - Service returns empty DTO list directly
        List<BookResponse> emptyResponses = Arrays.asList();
        when(bookService.findAllBooks()).thenReturn(emptyResponses);

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookService).findAllBooks();
    }

    // =====================================================
    // TESTS: GET /api/books/available
    // =====================================================

    @Test
    @DisplayName("GET /api/books/available - WHEN getting available books THEN return only available")
    void whenGettingAvailableBooks_thenReturnOnlyAvailable() throws Exception {
        // Given - Service returns available book DTOs directly
        List<BookResponse> availableResponses = Arrays.asList(response1);
        when(bookService.findAvailableBooks()).thenReturn(availableResponses);

        // When & Then
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Clean Code")))
                .andExpect(jsonPath("$[0].availableCopies", greaterThan(0)));

        verify(bookService).findAvailableBooks();
    }

    @Test
    @DisplayName("GET /api/books/available - WHEN no available books THEN return empty list")
    void whenNoAvailableBooks_thenReturnEmptyList() throws Exception {
        // Given - Service returns empty DTO list directly
        List<BookResponse> emptyResponses = Arrays.asList();
        when(bookService.findAvailableBooks()).thenReturn(emptyResponses);

        // When & Then
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookService).findAvailableBooks();
    }

    // =====================================================
    // TESTS: GET /api/books/{id}
    // =====================================================

    @Test
    @DisplayName("GET /api/books/{id} - WHEN book exists THEN return HTTP 200 with book")
    void whenGettingExistingBook_thenReturnHttp200WithBook() throws Exception {
        // Given - Service returns DTO directly
        when(bookService.findById(1L)).thenReturn(response1);

        // When & Then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.author", is("Robert Martin")))
                .andExpect(jsonPath("$.isbn", is("978-0132350884")))
                .andExpect(jsonPath("$.availableCopies", is(5)))
                .andExpect(jsonPath("$.totalCopies", is(5)));

        verify(bookService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/books/{id} - WHEN book does not exist THEN return HTTP 404")
    void whenGettingNonExistentBook_thenReturnHttp404() throws Exception {
        // Given - Service throws exception
        when(bookService.findById(99L)).thenThrow(new BookNotFoundException("Book not found with ID: 99"));

        // When & Then
        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Book not found")));

        verify(bookService).findById(99L);
    }

    // =====================================================
    // TESTS: POST /api/books
    // =====================================================

    @Test
    @DisplayName("POST /api/books - WHEN registering valid book THEN return HTTP 201 Created")
    void whenRegisteringValidBook_thenReturnHttp201() throws Exception {
        // Given - Service takes BookRequest and returns BookResponse directly
        BookRequest request = new BookRequest();
        request.setTitle("Design Patterns");
        request.setAuthor("Gang of Four");
        request.setIsbn("978-0201633610");
        request.setTotalCopies(3);

        BookResponse savedResponse = new BookResponse();
        savedResponse.setId(3L);
        savedResponse.setTitle("Design Patterns");
        savedResponse.setAuthor("Gang of Four");
        savedResponse.setIsbn("978-0201633610");
        savedResponse.setAvailableCopies(3);
        savedResponse.setTotalCopies(3);

        when(bookService.registerNewBook(any(BookRequest.class))).thenReturn(savedResponse);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("Design Patterns")))
                .andExpect(jsonPath("$.author", is("Gang of Four")))
                .andExpect(jsonPath("$.availableCopies", is(3)));

        verify(bookService).registerNewBook(any(BookRequest.class));
    }

    @Test
    @DisplayName("POST /api/books - WHEN registering book without copies THEN defaults applied and HTTP 201")
    void whenRegisteringBookWithoutCopies_thenDefaultsAppliedAndHttp201() throws Exception {
        // Given - Service applies defaults and returns response directly
        BookRequest request = new BookRequest();
        request.setTitle("Test Driven Development");
        request.setAuthor("Kent Beck");
        request.setIsbn("978-0321146530");
        // No copies specified

        BookResponse savedResponse = new BookResponse();
        savedResponse.setId(4L);
        savedResponse.setTitle("Test Driven Development");
        savedResponse.setAuthor("Kent Beck");
        savedResponse.setIsbn("978-0321146530");
        savedResponse.setAvailableCopies(1);  // Default applied by service
        savedResponse.setTotalCopies(1);      // Default applied by service

        when(bookService.registerNewBook(any(BookRequest.class))).thenReturn(savedResponse);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availableCopies", is(1)))
                .andExpect(jsonPath("$.totalCopies", is(1)));

        verify(bookService).registerNewBook(any(BookRequest.class));
    }


    @Test
    @DisplayName("GET /api/books/{id} - WHEN invalid ID format THEN return HTTP 400")
    void whenInvalidIdFormat_thenReturnHttp400() throws Exception {
        // When & Then - Trying to get book with non-numeric ID
        // Service should not be called for invalid input
        mockMvc.perform(get("/api/books/invalid"))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).findById(any());
    }
}

