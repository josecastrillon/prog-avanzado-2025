package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.service.LoanService;
import co.edu.uniremington.library.service.dto.LoanRequest;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.dto.LoanUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * En esta clase hago las pruebas unitarias del controlador LoanController.
 * Aquí no pruebo la lógica interna del servicio, sino que verifico
 * que el controlador llame correctamente los métodos del servicio
 * y devuelva las respuestas esperadas.
 */
class LoanControllerTest {

    private LoanService loanService;      // Simula el servicio (no real)
    private LoanController loanController; // Controlador que se va a probar

    /**
     * Antes de cada prueba, creo los mocks y el controlador.
     */
    @BeforeEach
    void setUp() {
        // Creo un mock del servicio
        loanService = mock(LoanService.class);

        // Creo el controlador pasándole el servicio simulado
        loanController = new LoanController(loanService);
    }

    /**
     * Prueba del método createLoan()
     * Aquí verifico que se cree un préstamo correctamente
     * y que el controlador devuelva el código 201 (CREATED).
     */
    @Test
    void createLoan() {
        // Datos simulados del request
        LoanRequest request = new LoanRequest();
        request.setBookId(1L);
        request.setUserId(2L);

        // Simulo la respuesta del servicio
        LoanResponse response = new LoanResponse();
        when(loanService.createLoan(1L, 2L)).thenReturn(response);

        // Ejecuto el método del controlador
        ResponseEntity<LoanResponse> result = loanController.createLoan(request);

        // Verifico que la respuesta no sea nula
        assertNotNull(result);

        // Verifico que el estado HTTP sea 201 (CREATED)
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Verifico que el cuerpo tenga el mismo objeto de respuesta
        assertEquals(response, result.getBody());

        // Verifico que se haya llamado el método correcto del servicio
        verify(loanService).createLoan(1L, 2L);
    }

    /**
     * Prueba del método returnBook()
     * Verifico que el controlador llame al servicio para devolver un libro
     * y retorne correctamente el resultado con código 200 (OK).
     */
    @Test
    void returnBook() {
        // Simulo la respuesta del servicio
        LoanResponse response = new LoanResponse();
        when(loanService.returnBook(1L)).thenReturn(response);

        // Llamo al controlador
        ResponseEntity<LoanResponse> result = loanController.returnBook(1L);

        // Verifico resultados
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        // Compruebo que el método del servicio fue llamado correctamente
        verify(loanService).returnBook(1L);
    }

    /**
     * Prueba del método updateLoan()
     * Aquí verifico que el controlador reciba un LoanUpdateRequest
     * y llame al servicio para actualizar el préstamo.
     */
    @Test
    void updateLoan() {
        // Datos del request
        LoanUpdateRequest request = new LoanUpdateRequest();
        request.setActive(false);

        // Simulo la respuesta
        LoanResponse response = new LoanResponse();
        when(loanService.updateLoan(1L, request)).thenReturn(response);

        // Ejecuto el método
        ResponseEntity<LoanResponse> result = loanController.updateLoan(1L, request);

        // Verificaciones
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(loanService).updateLoan(1L, request);
    }

    /**
     * Prueba del método getAllLoans()
     * Verifico que el controlador devuelva todos los préstamos
     * llamando correctamente al servicio.
     */
    @Test
    void getAllLoans() {
        // Simulo la lista de respuesta
        List<LoanResponse> loans = List.of(new LoanResponse());
        when(loanService.findAllLoans()).thenReturn(loans);

        // Llamo al método
        ResponseEntity<List<LoanResponse>> result = loanController.getAllLoans();

        // Verifico
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(loans, result.getBody());
        verify(loanService).findAllLoans();
    }

    /**
     * Prueba del método getActiveLoans()
     * Aquí compruebo que el controlador obtenga los préstamos activos.
     */
    @Test
    void getActiveLoans() {
        List<LoanResponse> activeLoans = List.of(new LoanResponse());
        when(loanService.findActiveLoans()).thenReturn(activeLoans);

        ResponseEntity<List<LoanResponse>> result = loanController.getActiveLoans();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(activeLoans, result.getBody());
        verify(loanService).findActiveLoans();
    }

    /**
     * Prueba del método getLoansByUser()
     * Verifico que el controlador obtenga correctamente los préstamos
     * pertenecientes a un usuario específico.
     */
    @Test
    void getLoansByUser() {
        List<LoanResponse> userLoans = List.of(new LoanResponse());
        when(loanService.findLoansByUser(10L)).thenReturn(userLoans);

        ResponseEntity<List<LoanResponse>> result = loanController.getLoansByUser(10L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userLoans, result.getBody());
        verify(loanService).findLoansByUser(10L);
    }
}
