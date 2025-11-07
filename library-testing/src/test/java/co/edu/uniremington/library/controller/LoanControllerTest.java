package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.domain.exception.LoanNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import co.edu.uniremington.library.service.LoanService;
import co.edu.uniremington.library.service.dto.LoanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanResponse loan1;
    private LoanResponse loan2;


    @BeforeEach
    void setUp() {
    //Simulacion de prestamo

        loan1 = new LoanResponse();
        loan1.setId(1L);
        loan1.setBookTitle("Cien años de soledad");
        loan1.setUserName("Maria Bueno");
        loan1.setLoanDate(LocalDate.of(2025, 10, 1));
        loan1.setReturnDate(LocalDate.of(2025, 10, 15));
        loan1.setActive(true);

        loan2 = new LoanResponse();
        loan2.setId(2L);
        loan2.setBookTitle("Amor en los tiempos del colera");
        loan2.setUserName("Maria Bueno");
        loan2.setLoanDate(LocalDate.of(2025, 10, 1));
        loan2.setReturnDate(LocalDate.of(2025, 10, 15));
        loan2.setActive(true);

    }

    // =====================================================
    // TESTS: GET /api/loans
    // =====================================================

    @Test
    @DisplayName("CUANDO se obtengan todos los préstamos, ENTONCES devolver HTTP 200 con la lista")
    void createLoan_whenGettingAllLoans_thenReturnHttp200WithList() throws Exception {
        //El servicio devuelve DTOs directamente
        //Given
        List<LoanResponse> allLoans = Arrays.asList(loan1, loan2);
        when(loanService.findAllLoans()).thenReturn(allLoans);

        //El controlador devuelve los DTO directamente del servicio.
        //When - The
        mockMvc.perform(get("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookTitle", is("Cien años de soledad")))
                .andExpect(jsonPath("$[0].userName", is("Maria Bueno")))
                .andExpect(jsonPath("$[0].active", is(true)))
                .andExpect(jsonPath("$[1].bookTitle", is("Amor en los tiempos del colera")))
                .andExpect(jsonPath("$[1].userName", is("Maria Bueno")))
                .andExpect(jsonPath("$[1].active", is(true)));

        //Verify
        verify(loanService, times(1)).findAllLoans();

    }

    @Test
    @DisplayName("Si no existen préstamos, devuelve una lista vacía.")
    void whenNoLoansExist_thenReturnEmptyList() throws Exception {
        //Given
        List<LoanResponse> emptyResponse = Arrays.asList();
        when(loanService.findAllLoans()).thenReturn(emptyResponse);

        //When - Then
        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        //Verify
        verify(loanService).findAllLoans();


    }

    // =====================================================
    // TESTS: GET /api/loans/active
    // =====================================================
    @Test
    @DisplayName("Si se obtienen los préstamos activos, devuelve solo los activos.")
    void ReturnBook_whenGettingActiveLoans_thenReturnOnlyActive() throws Exception{
        //Given
        List<LoanResponse> activeResponses = Arrays.asList(loan1);
        when(loanService.findActiveLoans()).thenReturn(activeResponses);

        //When - Then
        mockMvc.perform(get("/api/loans/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookTitle", is("Cien años de soledad")))
                .andExpect(jsonPath("$[0].userName", is("Maria Bueno")))
                .andExpect(jsonPath("$[0].active", is(true)));

        //Verify
        verify(loanService).findActiveLoans();
    }

    @Test
    @DisplayName("Si no hay préstamos activos, devolver una lista vacía")
    void ReturnLoans_whenNoActiveLoans_thenReturnEmptyList() throws Exception{
        //Given
        List<LoanResponse> emptyResponses = Arrays.asList();
        when(loanService.findActiveLoans()).thenReturn(emptyResponses);

        //When - Then
        mockMvc.perform(get("/api/loans/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        //Verify
        verify(loanService).findActiveLoans();
    }

    // =====================================================
    // TESTS: GET /api/loans/{id}
    // =====================================================

    @Test
    @DisplayName("Cuando el usuario tenga préstamos, devolver HTTP 200 con la lista de préstamos.")
    void GetId_whenGettingLoansByUser_thenReturnHttp200WithListOfLoans() throws Exception{
        //Given
        //List<LoanResponse> userLoans = Arrays.asList(loan1, loan2);
        when(loanService.findById(  1L)).thenReturn(loan1);

        //When - Then Error que tuve- Fue que en la cadena de texto puso 1l, debo tener ene cuenta que en esta no recibe la L solo numero
        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.bookTitle", is("Cien años de soledad")))
                .andExpect(jsonPath("$.userName", is("Maria Bueno")))
                .andExpect(jsonPath("$.loanDate", is("2025-10-01")))
                .andExpect(jsonPath("$.returnDate", is("2025-10-15")))
                .andExpect(jsonPath("$.active", is(true)));

        //Verify
        verify(loanService).findById(1L);
    }

    //Cuando se obtiene un préstamo no existente, devolver HTTP 404.
    @Test
    @DisplayName("Cuando el préstamo no existe, devuelve HTTP 404.")
    void GetId_whenGettingNonExistentLoan_thenReturnHttp404() throws Exception {
        //Given
        when(loanService.findById(99L)).thenThrow(new LoanNotFoundException("Loan not found with ID: 99"));

        //When - Then
        mockMvc.perform(get("/api/loans/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Loan not found ")));

        //Verify
        verify(loanService).findById(99L);

    }
    // =====================================================
    // TESTS: POST /api/loans
    // =====================================================

    @Test
    @DisplayName("")
    void f(){

    }
}
