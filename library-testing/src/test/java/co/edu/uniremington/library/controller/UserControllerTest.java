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

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    private UserResponse user1;
    private UserResponse user2;

    @BeforeEach
    void setUp() {
        user1 = new UserResponse();
        user1.setId(1L);
        user1.setName("Maria Bueno");
        user1.setEmail("bvmafe@hotmail.com");
        user1.setHasFines(true);

        user2 = new UserResponse();
        user2.setId(2L);
        user2.setName("Juan Ramirez");
        user2.setEmail("jdramirez@gmail.com");
        user2.setHasFines(true);

    }

    @Test
    @DisplayName("Cuando se obtienen todos los usuarios, se devuelve un código HTTP 200 con la lista.")
    void getAllUsers_whenGettingAllUsers_thenReturnHttp200WithList() throws Exception {
        //Given
        List<UserResponse> allResponses = Arrays.asList(user1, user2);
        when(userService.findAllUsers()).thenReturn(allResponses);

        //When - Then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Maria Bueno")))
                .andExpect(jsonPath("$[0].hasFines", is(true)));

        //Verify
        verify(userService).findAllUsers();

    }

    @Test
    @DisplayName("Cuando un usuario ya existe, se devuelve un código HTTP 200 con el usuario.")
    void getUserById_whenGettingExistingUser_thenReturnHttp200WithUser() throws Exception{
        //Given
        when(userService.findById(1L)).thenReturn(user1);

        //When - Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Maria Bueno")))
                .andExpect(jsonPath("$.email", is("bvmafe@hotmail.com")))
                .andExpect(jsonPath("$.hasFines", is(true)));

        //Verify
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Cuando el usuario no existe, se devuelve un código HTTP 404.")
    void getUserById_whenGettingNonExistentUser_thenReturnHttp404() throws Exception {
        //Given
        when(userService.findById(99L)).thenThrow(new UserNotFoundException("Usuario no encontrado con ID: 99"));

        //When - Then
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Usuario no encontrado")));

        //Verify
        verify(userService).findById(99L);
    }

    @Test
    @DisplayName("Al registrar un nuevo usuario, se debe devolver un código HTTP 201 con los datos del usuario.")
    void registerUser_() throws Exception {
        UserRequest request = new UserRequest("Andres Felipe", "andresf@gmail.com");
        UserResponse response = new UserResponse();
        response.setId(3L);
        response.setName("Andres Felipe Diaz");
        response.setEmail("andresf@gmail.com");
        response.setHasFines(false);

        when(userService.registerNewUser(any(UserRequest.class))).thenReturn(response);

        //When - Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Andres Felipe",
                                "email": "andresf@gmail.com"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Andres Felipe Diaz")))
                .andExpect(jsonPath("$.email", is("andresf@gmail.com")))
                .andExpect(jsonPath("$.hasFines", is(false)));

        //Verify
        verify(userService).registerNewUser(any(UserRequest.class));
    }

    @Test
    @DisplayName("Al añadir multas, devuelve HTTP 200 con el usuario actualizado.")
    void addFines_whenAddingFines_thenReturnHttp200WithUpdatedUser() throws Exception{
        //Given
        user1.setHasFines(true);
        when(userService.addFines(1L)).thenReturn(user1);

        //When - Then
        mockMvc.perform(post("/api/users/1/fines")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.hasFines", is(true)));

        //Verify
        verify(userService).addFines(1L);
    }

    @Test
    @DisplayName("Al eliminar multas, devolver HTTP 200 con el usuario actualizado.")
    void clearFines_whenClearingFines_thenReturnHttp200WithUpdatedUser() throws Exception{
        //Given
        user1.setHasFines(false);
        when(userService.clearFines(1L)).thenReturn(user1);

        //When - Then
        mockMvc.perform(delete("/api/users/1/fines")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.hasFines", is(false)));

        //Verify
        verify(userService).clearFines(1L);
    }
}