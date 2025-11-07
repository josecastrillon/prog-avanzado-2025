package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.service.LoanService;
import co.edu.uniremington.library.service.dto.LoanRequest;
import co.edu.uniremington.library.service.dto.LoanResponse;
import co.edu.uniremington.library.service.dto.LoanUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanResponse = new LoanResponse();
        loanResponse.setId(1L);
        loanResponse.setBookTitle("El Principito");
        loanResponse.setUserName("Juan");
        loanResponse.setActive(true);
    }

    @Test
    void createLoan() {
        // Arrange
        LoanRequest request = new LoanRequest();
        request.setBookId(10L);
        request.setUserId(5L);

        when(loanService.createLoan(request.getBookId(), request.getUserId())).thenReturn(loanResponse);

        // Act
        ResponseEntity<LoanResponse> response = loanController.createLoan(request);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("El Principito", response.getBody().getBookTitle());
        verify(loanService, times(1)).createLoan(10L, 5L);
    }

    @Test
    void returnBook() {
        // Arrange
        loanResponse.setActive(false);
        when(loanService.returnBook(1L)).thenReturn(loanResponse);

        // Act
        ResponseEntity<LoanResponse> response = loanController.returnBook(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().getActive());
        verify(loanService, times(1)).returnBook(1L);
    }

    @Test
    void updateLoan() {
        // Arrange
        LoanUpdateRequest updateRequest = new LoanUpdateRequest();
        updateRequest.setActive(false);
        when(loanService.updateLoan(1L, updateRequest)).thenReturn(loanResponse);

        // Act
        ResponseEntity<LoanResponse> response = loanController.updateLoan(1L, updateRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(loanService, times(1)).updateLoan(1L, updateRequest);
    }

    @Test
    void getAllLoans() {
        // Arrange
        when(loanService.findAllLoans()).thenReturn(List.of(loanResponse));

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getAllLoans();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(loanService, times(1)).findAllLoans();
    }

    @Test
    void getActiveLoans() {
        // Arrange
        when(loanService.findActiveLoans()).thenReturn(List.of(loanResponse));

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getActiveLoans();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().get(0).getActive());
        verify(loanService, times(1)).findActiveLoans();
    }

    @Test
    void getLoansByUser() {
        // Arrange
        when(loanService.findLoansByUser(5L)).thenReturn(List.of(loanResponse));

        // Act
        ResponseEntity<List<LoanResponse>> response = loanController.getLoansByUser(5L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Juan", response.getBody().get(0).getUserName());
        verify(loanService, times(1)).findLoansByUser(5L);
    }
}
