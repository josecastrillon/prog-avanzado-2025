package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.service.UserService;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Juan");
        userResponse.setHasFines(false);
    }

    @Test
    void getAllUsers() {
        // Arrange
        when(userService.findAllUsers()).thenReturn(List.of(userResponse));

        // Act
        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void getUserById() {
        // Arrange
        when(userService.findById(1L)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Juan", response.getBody().getName());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void registerUser() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setName("Pedro");
        when(userService.registerNewUser(request)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.registerUser(request);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(userService, times(1)).registerNewUser(request);
    }

    @Test
    void addFines() {
        // Arrange
        userResponse.setHasFines(true);
        when(userService.addFines(1L)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.addFines(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getHasFines());
        verify(userService, times(1)).addFines(1L);
    }

    @Test
    void clearFines() {
        // Arrange
        userResponse.setHasFines(false);
        when(userService.clearFines(1L)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.clearFines(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().getHasFines());
        verify(userService, times(1)).clearFines(1L);
    }
}
