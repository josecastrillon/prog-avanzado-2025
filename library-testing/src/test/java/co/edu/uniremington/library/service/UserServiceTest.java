package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.UserNotFoundException;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.UserRepository;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import co.edu.uniremington.library.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Leidy");
        user.setEmail("leidy@example.com");
        user.setHasFines(false);

        userRequest = new UserRequest();
        userRequest.setName("Leidy");
        userRequest.setEmail("leidy@example.com");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Leidy");
        userResponse.setEmail("leidy@example.com");
        userResponse.setHasFines(false);
    }

    @Test
    @DisplayName("Debería devolver un usuario cuando se busca por ID existente")
    void findById() {
        //Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("Leidy", result.getName());
        verify(userRepository).findById(1L);

    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe al buscar por ID")
    void findById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Debería devolver la lista de todos los usuarios")
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseList(List.of(user))).thenReturn(List.of(userResponse));

        List<UserResponse> result = userService.findAllUsers();

        assertEquals(1, result.size());
        assertEquals("Leidy", result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Debería registrar un nuevo usuario correctamente")
    void registerNewUser() {
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.registerNewUser(userRequest);

        assertNotNull(result);
        assertEquals("Leidy", result.getName());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Debería devolver la entidad usuario cuando existe")
    void findEntityById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findEntityById(1L);

        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción si no encuentra la entidad usuario")
    void findEntityById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findEntityById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Debería retornar true si el usuario tiene multas")
    void hasFines_True() {
        user.setHasFines(true);

        assertTrue(userService.hasFines(user));
    }

    @Test
    @DisplayName("Debería retornar false si el usuario no tiene multas")
    void hasFines_False() {
        user.setHasFines(false);

        assertFalse(userService.hasFines(user));
    }

    @Test
    @DisplayName("Debería marcar al usuario con multas correctamente")
    void addFines() {
        user.setHasFines(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.addFines(1L);

        assertNotNull(result);
        verify(userRepository).save(user);
        assertTrue(user.getHasFines());
    }

    @Test
    @DisplayName("Debería limpiar las multas del usuario correctamente")
    void clearFines() {
        user.setHasFines(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.clearFines(1L);

        assertNotNull(result);
        verify(userRepository).save(user);
        assertFalse(user.getHasFines());
    }

    @Test
    @DisplayName("Debería devolver true si el usuario es elegible para préstamo (sin multas)")
    void isEligibleForLoan_True() {
        user.setHasFines(false);

        assertTrue(userService.isEligibleForLoan(user));
    }

    @Test
    @DisplayName("Debería devolver false si el usuario no es elegible (tiene multas)")
    void isEligibleForLoan_False() {
        user.setHasFines(true);

        assertFalse(userService.isEligibleForLoan(user));
    }
}
