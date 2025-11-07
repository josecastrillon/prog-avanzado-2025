package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.domain.exception.UserNotFoundException;
import co.edu.uniremington.library.service.UserService;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for UserController.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse response1;
    private UserResponse response2;

    @BeforeEach
    void setUp() {
        // Setup DTOs - Service returns DTOs directly!
        response1 = new UserResponse();
        response1.setId(1L);
        response1.setName("Juan Pérez");
        response1.setEmail("juan.perez@example.com");
        response1.setHasFines(false);

        response2 = new UserResponse();
        response2.setId(2L);
        response2.setName("María García");
        response2.setEmail("maria.garcia@example.com");
        response2.setHasFines(true);
    }

    // =====================================================
    // TESTS: GET /api/users
    // =====================================================

    @Test
    @DisplayName("GET /api/users - WHEN getting all users THEN return HTTP 200 with list")
    void whenGettingAllUsers_thenReturnHttp200WithList() throws Exception {
        // Given - Service returns DTOs directly
        List<UserResponse> allResponses = Arrays.asList(response1, response2);
        when(userService.findAllUsers()).thenReturn(allResponses);

        // When & Then - Controller returns DTOs directly from service
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Juan Pérez")))
                .andExpect(jsonPath("$[0].email", is("juan.perez@example.com")))
                .andExpect(jsonPath("$[0].hasFines", is(false)))
                .andExpect(jsonPath("$[1].name", is("María García")))
                .andExpect(jsonPath("$[1].hasFines", is(true)));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    @DisplayName("GET /api/users - WHEN no users exist THEN return HTTP 200 with empty list")
    void whenNoUsers_thenReturnHttp200WithEmptyList() throws Exception {
        // Given - Service returns empty DTO list directly
        List<UserResponse> emptyResponses = Arrays.asList();
        when(userService.findAllUsers()).thenReturn(emptyResponses);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService).findAllUsers();
    }

    // =====================================================
    // TESTS: GET /api/users/{id}
    // =====================================================

    @Test
    @DisplayName("GET /api/users/{id} - WHEN user exists THEN return HTTP 200 with user")
    void whenGettingExistingUser_thenReturnHttp200WithUser() throws Exception {
        // Given - Service returns DTO directly
        when(userService.findById(1L)).thenReturn(response1);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Juan Pérez")))
                .andExpect(jsonPath("$.email", is("juan.perez@example.com")))
                .andExpect(jsonPath("$.hasFines", is(false)));

        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - WHEN user does not exist THEN return HTTP 404")
    void whenGettingNonExistentUser_thenReturnHttp404() throws Exception {
        // Given - Service throws exception
        when(userService.findById(99L)).thenThrow(new UserNotFoundException("User not found with ID: 99"));

        // When & Then
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));

        verify(userService).findById(99L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - WHEN invalid ID format THEN return HTTP 400")
    void whenInvalidIdFormat_thenReturnHttp400() throws Exception {
        // When & Then - Trying to get user with non-numeric ID
        mockMvc.perform(get("/api/users/invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findById(any());
    }

    // =====================================================
    // TESTS: POST /api/users
    // =====================================================

    @Test
    @DisplayName("POST /api/users - WHEN registering valid user THEN return HTTP 201 Created")
    void whenRegisteringValidUser_thenReturnHttp201() throws Exception {
        // Given - Service takes UserRequest and returns UserResponse directly
        UserRequest request = new UserRequest();
        request.setName("Carlos López");
        request.setEmail("carlos.lopez@example.com");

        UserResponse savedResponse = new UserResponse();
        savedResponse.setId(3L);
        savedResponse.setName("Carlos López");
        savedResponse.setEmail("carlos.lopez@example.com");
        savedResponse.setHasFines(false); // Default value

        when(userService.registerNewUser(any(UserRequest.class))).thenReturn(savedResponse);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Carlos López")))
                .andExpect(jsonPath("$.email", is("carlos.lopez@example.com")))
                .andExpect(jsonPath("$.hasFines", is(false)));

        verify(userService).registerNewUser(any(UserRequest.class));
    }

    @Test
    @DisplayName("POST /api/users - WHEN registering user with invalid data THEN return HTTP 400")
    void whenRegisteringUserWithInvalidData_thenReturnHttp400() throws Exception {
        // Given - Empty request (invalid)
        UserRequest request = new UserRequest();
        // No name or email set

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerNewUser(any(UserRequest.class));
    }

    // =====================================================
    // TESTS: POST /api/users/{id}/fines
    // =====================================================

    @Test
    @DisplayName("POST /api/users/{id}/fines - WHEN adding fines to user THEN return HTTP 200 with updated user")
    void whenAddingFinesToUser_thenReturnHttp200WithUpdatedUser() throws Exception {
        // Given
        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Juan Pérez");
        updatedResponse.setEmail("juan.perez@example.com");
        updatedResponse.setHasFines(true); // Fines added

        when(userService.addFines(1L)).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(post("/api/users/1/fines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.hasFines", is(true)));

        verify(userService).addFines(1L);
    }

    @Test
    @DisplayName("POST /api/users/{id}/fines - WHEN adding fines to non-existent user THEN return HTTP 404")
    void whenAddingFinesToNonExistentUser_thenReturnHttp404() throws Exception {
        // Given
        when(userService.addFines(99L)).thenThrow(new UserNotFoundException("User not found with ID: 99"));

        // When & Then
        mockMvc.perform(post("/api/users/99/fines"))
                .andExpect(status().isNotFound());

        verify(userService).addFines(99L);
    }

    // =====================================================
    // TESTS: DELETE /api/users/{id}/fines
    // =====================================================

    @Test
    @DisplayName("DELETE /api/users/{id}/fines - WHEN clearing fines from user THEN return HTTP 200 with updated user")
    void whenClearingFinesFromUser_thenReturnHttp200WithUpdatedUser() throws Exception {
        // Given
        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(2L);
        updatedResponse.setName("María García");
        updatedResponse.setEmail("maria.garcia@example.com");
        updatedResponse.setHasFines(false); // Fines cleared

        when(userService.clearFines(2L)).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(delete("/api/users/2/fines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.hasFines", is(false)));

        verify(userService).clearFines(2L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id}/fines - WHEN clearing fines from non-existent user THEN return HTTP 404")
    void whenClearingFinesFromNonExistentUser_thenReturnHttp404() throws Exception {
        // Given
        when(userService.clearFines(99L)).thenThrow(new UserNotFoundException("User not found with ID: 99"));

        // When & Then
        mockMvc.perform(delete("/api/users/99/fines"))
                .andExpect(status().isNotFound());

        verify(userService).clearFines(99L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id}/fines - WHEN invalid ID format THEN return HTTP 400")
    void whenClearingFinesWithInvalidIdFormat_thenReturnHttp400() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/invalid/fines"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).clearFines(any());
    }
}