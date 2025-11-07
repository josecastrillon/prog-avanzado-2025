package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.service.LoanService;
import co.edu.uniremington.library.service.dto.LoanRequest;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.dto.LoanUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanResponse response1;
    private LoanResponse response2;

    @BeforeEach
    void setUp() {
        // Setup DTOs - Service returns DTOs directly!
        response1 = new LoanResponse();
        response1.setId(1L);
        response1.setLoanDate(LocalDate.now().minusDays(5));
        response1.setReturnDate(null);
        response1.setActive(true);

        response2 = new LoanResponse();
        response2.setId(2L);
        response2.setLoanDate(LocalDate.now().minusDays(10));
        response2.setReturnDate(LocalDate.now());
        response2.setActive(false);
    }

    // =====================================================
    // TESTS: POST /api/loans
    // =====================================================

    @Test
    @DisplayName("POST /api/loans - WHEN creating valid loan THEN return HTTP 201 Created")
    void whenCreatingValidLoan_thenReturnHttp201() throws Exception {
        // Given
        LoanRequest request = new LoanRequest(10L, 100L);

        when(loanService.createLoan(10L, 100L)).thenReturn(response1);

        // When & Then
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.bookId", is(10)))
                .andExpect(jsonPath("$.userId", is(100)))
                .andExpect(jsonPath("$.active", is(true)));

        verify(loanService, times(1)).createLoan(10L, 100L);
    }

    @Test
    @DisplayName("POST /api/loans - WHEN creating loan with invalid data THEN return HTTP 400")
    void whenCreatingLoanWithInvalidData_thenReturnHttp400() throws Exception {
        // Given - Request sin datos (inválido)
        LoanRequest request = new LoanRequest(null, null);

        // When & Then
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(loanService, never()).createLoan(any(Long.class), any(Long.class));
    }

    // =====================================================
    // TESTS: POST /api/loans/{id}/return
    // =====================================================

    @Test
    @DisplayName("POST /api/loans/{id}/return - WHEN returning book THEN return HTTP 200 with updated loan")
    void whenReturningBook_thenReturnHttp200WithUpdatedLoan() throws Exception {
        // Given
        when(loanService.returnBook(1L)).thenReturn(response2);

        // When & Then
        mockMvc.perform(post("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.active", is(false)))
                .andExpect(jsonPath("$.returnDate", notNullValue()));

        verify(loanService, times(1)).returnBook(1L);
    }

    @Test
    @DisplayName("POST /api/loans/{id}/return - WHEN returning non-existent loan THEN return HTTP 404")
    void whenReturningNonExistentLoan_thenReturnHttp404() throws Exception {
        // Given
        when(loanService.returnBook(99L)).thenThrow(new RuntimeException("Loan not found"));

        // When & Then
        mockMvc.perform(post("/api/loans/99/return"))
                .andExpect(status().isNotFound());

        verify(loanService, times(1)).returnBook(99L);
    }

    // =====================================================
    // TESTS: PATCH /api/loans/{id}
    // =====================================================

    @Test
    @DisplayName("PATCH /api/loans/{id} - WHEN updating loan partially THEN return HTTP 200 with updated loan")
    void whenUpdatingLoanPartially_thenReturnHttp200WithUpdatedLoan() throws Exception {
        // Given
        LoanUpdateRequest updateRequest = new LoanUpdateRequest();
        updateRequest.setActive(false);
        updateRequest.setReturnDate(LocalDate.now().toString()); // Convertir a String si es necesario

        when(loanService.updateLoan(eq(1L), any(LoanUpdateRequest.class))).thenReturn(response2);

        // When & Then
        mockMvc.perform(patch("/api/loans/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.active", is(false)));

        verify(loanService, times(1)).updateLoan(eq(1L), any(LoanUpdateRequest.class));
    }

    @Test
    @DisplayName("PATCH /api/loans/{id} - WHEN updating non-existent loan THEN return HTTP 404")
    void whenUpdatingNonExistentLoan_thenReturnHttp404() throws Exception {
        // Given
        LoanUpdateRequest updateRequest = new LoanUpdateRequest();
        updateRequest.setActive(false);

        when(loanService.updateLoan(eq(99L), any(LoanUpdateRequest.class)))
                .thenThrow(new RuntimeException("Loan not found"));

        // When & Then
        mockMvc.perform(patch("/api/loans/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(loanService, times(1)).updateLoan(eq(99L), any(LoanUpdateRequest.class));
    }

    // =====================================================
    // TESTS: GET /api/loans
    // =====================================================

    @Test
    @DisplayName("GET /api/loans - WHEN getting all loans THEN return HTTP 200 with list")
    void whenGettingAllLoans_thenReturnHttp200WithList() throws Exception {
        // Given
        List<LoanResponse> allResponses = Arrays.asList(response1, response2);
        when(loanService.findAllLoans()).thenReturn(allResponses);

        // When & Then
        mockMvc.perform(get("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].active", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].active", is(false)));

        verify(loanService, times(1)).findAllLoans();
    }

    @Test
    @DisplayName("GET /api/loans - WHEN no loans exist THEN return HTTP 200 with empty list")
    void whenNoLoans_thenReturnHttp200WithEmptyList() throws Exception {
        // Given
        List<LoanResponse> emptyResponses = Arrays.asList();
        when(loanService.findAllLoans()).thenReturn(emptyResponses);

        // When & Then
        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(loanService).findAllLoans();
    }

    // =====================================================
    // TESTS: GET /api/loans/active
    // =====================================================

    @Test
    @DisplayName("GET /api/loans/active - WHEN getting active loans THEN return only active loans")
    void whenGettingActiveLoans_thenReturnOnlyActiveLoans() throws Exception {
        // Given
        List<LoanResponse> activeResponses = Arrays.asList(response1);
        when(loanService.findActiveLoans()).thenReturn(activeResponses);

        // When & Then
        mockMvc.perform(get("/api/loans/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].active", is(true)));

        verify(loanService).findActiveLoans();
    }

    @Test
    @DisplayName("GET /api/loans/active - WHEN no active loans THEN return empty list")
    void whenNoActiveLoans_thenReturnEmptyList() throws Exception {
        // Given
        List<LoanResponse> emptyResponses = Arrays.asList();
        when(loanService.findActiveLoans()).thenReturn(emptyResponses);

        // When & Then
        mockMvc.perform(get("/api/loans/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(loanService).findActiveLoans();
    }

    // =====================================================
    // TESTS: GET /api/loans/user/{userId}
    // =====================================================

    @Test
    @DisplayName("GET /api/loans/user/{userId} - WHEN getting loans by user THEN return user's loans")
    void whenGettingLoansByUser_thenReturnUserLoans() throws Exception {
        // Given
        List<LoanResponse> userResponses = Collections.singletonList(response1);
        when(loanService.findLoansByUser(100L)).thenReturn(userResponses);

        // When & Then
        mockMvc.perform(get("/api/loans/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(100)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(loanService).findLoansByUser(100L);
    }

    @Test
    @DisplayName("GET /api/loans/user/{userId} - WHEN user has no loans THEN return empty list")
    void whenUserHasNoLoans_thenReturnEmptyList() throws Exception {
        // Given
        List<LoanResponse> emptyResponses = List.of();
        when(loanService.findLoansByUser(999L)).thenReturn(emptyResponses);

        // When & Then
        mockMvc.perform(get("/api/loans/user/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(loanService).findLoansByUser(999L);
    }

    @Test
    @DisplayName("GET /api/loans/{id} - WHEN invalid ID format THEN return HTTP 400")
    void whenInvalidIdFormat_thenReturnHttp400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/loans/invalid"))
                .andExpect(status().isBadRequest());

        verify(loanService, never()).findById(any());
    }
}



