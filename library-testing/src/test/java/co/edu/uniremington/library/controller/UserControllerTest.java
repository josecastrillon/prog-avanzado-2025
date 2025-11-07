package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.service.UserService;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * En esta clase hago las pruebas unitarias del controlador UserController.
 * Aquí verifico que el controlador llame correctamente al servicio
 * y que devuelva las respuestas esperadas (códigos HTTP y datos correctos).
 * No pruebo la lógica del servicio, solo la parte del controlador.
 */
class UserControllerTest {

    private UserService userService;       // Simula el servicio (mock)
    private UserController userController; // Controlador que voy a probar

    /**
     * Antes de cada prueba creo los mocks y el controlador.
     */
    @BeforeEach
    void setUp() {
        // Creo un mock del servicio UserService
        userService = mock(UserService.class);

        // Paso el servicio simulado al controlador
        userController = new UserController(userService);
    }

    /**
     * Prueba del método getAllUsers()
     * Verifico que el controlador llame correctamente al servicio
     * y devuelva la lista completa de usuarios con código 200 (OK).
     */
    @Test
    void getAllUsers() {
        // Simulo una lista de usuarios
        List<UserResponse> userList = List.of(new UserResponse());
        when(userService.findAllUsers()).thenReturn(userList);

        // Llamo al método del controlador
        ResponseEntity<List<UserResponse>> result = userController.getAllUsers();

        // Verifico resultados
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userList, result.getBody());

        // Compruebo que el servicio fue llamado
        verify(userService).findAllUsers();
    }

    /**
     * Prueba del método getUserById()
     * Verifico que el controlador reciba un ID, llame al servicio
     * y devuelva el usuario correspondiente.
     */
    @Test
    void getUserById() {
        // Simulo un usuario de respuesta
        UserResponse userResponse = new UserResponse();
        when(userService.findById(1L)).thenReturn(userResponse);

        // Llamo al método del controlador
        ResponseEntity<UserResponse> result = userController.getUserById(1L);

        // Verifico resultados
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService).findById(1L);
    }

    /**
     * Prueba del método registerUser()
     * Verifico que el controlador cree un nuevo usuario correctamente
     * y devuelva código 201 (CREATED).
     */
    @Test
    void registerUser() {
        // Creo un objeto UserRequest simulado
        UserRequest request = new UserRequest();

        // Simulo la respuesta que debería devolver el servicio
        UserResponse userResponse = new UserResponse();
        when(userService.registerNewUser(request)).thenReturn(userResponse);

        // Ejecuto el método del controlador
        ResponseEntity<UserResponse> result = userController.registerUser(request);

        // Verifico los resultados esperados
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService).registerNewUser(request);
    }

    /**
     * Prueba del método addFines()
     * Verifico que el controlador agregue multas al usuario
     * y devuelva el usuario actualizado con código 200 (OK).
     */
    @Test
    void addFines() {
        // Simulo la respuesta del servicio
        UserResponse userResponse = new UserResponse();
        when(userService.addFines(5L)).thenReturn(userResponse);

        // Llamo al método del controlador
        ResponseEntity<UserResponse> result = userController.addFines(5L);

        // Verificaciones
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService).addFines(5L);
    }

    /**
     * Prueba del método clearFines()
     * Verifico que el controlador quite las multas de un usuario
     * y devuelva la respuesta con código 200 (OK).
     */
    @Test
    void clearFines() {
        // Simulo la respuesta del servicio
        UserResponse userResponse = new UserResponse();
        when(userService.clearFines(5L)).thenReturn(userResponse);

        // Ejecuto el método del controlador
        ResponseEntity<UserResponse> result = userController.clearFines(5L);

        // Verifico resultados
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService).clearFines(5L);
    }
}
