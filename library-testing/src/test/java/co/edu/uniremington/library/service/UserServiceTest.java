package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.UserNotFoundException;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.UserRepository;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import co.edu.uniremington.library.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceTest {

    // Simula el repositorio (no accede realmente a la base de datos)
    @Mock
    private UserRepository userRepository;

    // Simula el mapper que convierte entre entidades y DTOs
    @Mock
    private UserMapper userMapper;

    // Crea una instancia real de UserService, pero con las dependencias mockeadas
    @InjectMocks
    private UserService userService;

    // Objetos de ejemplo que se reutilizan en varias pruebas
    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    /**
     * Se ejecuta antes de cada prueba. Inicializa los mocks y crea
     * objetos de ejemplo para usar en los distintos tests.
     */
    @BeforeEach
    void setUp() {
        // Inicializa las anotaciones de Mockito (@Mock y @InjectMocks)
        MockitoAnnotations.openMocks(this);

        // Crea un usuario de ejemplo
        user = new User();
        user.setId(1L);
        user.setName("Juan");
        user.setHasFines(false);

        // Crea un DTO de solicitud de usuario
        userRequest = new UserRequest();
        userRequest.setName("Juan");

        // Crea un DTO de respuesta
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Juan");
        userResponse.setHasFines(false);
    }

    /**
     * Prueba que el método findById() devuelva un usuario correctamente
     * cuando el ID existe en el repositorio.
     */
    @Test
    void findById_shouldReturnUserResponse_whenUserExists() {
        // Simula que el repositorio encuentra al usuario con ID 1
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Simula que el mapper convierte la entidad a un DTO de respuesta
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Llama al método del servicio
        UserResponse response = userService.findById(1L);

        // Verifica que la respuesta no sea nula y tenga los datos esperados
        assertNotNull(response);
        assertEquals("Juan", response.getName());

        // Verifica que el repositorio fue llamado con el ID correcto
        verify(userRepository).findById(1L);
    }

    /**
     * Prueba que el método findById() lance una excepción UserNotFoundException
     * cuando el usuario con el ID indicado no existe.
     */
    @Test
    void findById_shouldThrowException_whenUserDoesNotExist() {
        // Simula que el repositorio no encuentra al usuario
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica que se lanza la excepción esperada
        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    /**
     * Prueba que el método findAllUsers() devuelva una lista con usuarios.
     */
    @Test
    void findAllUsers_shouldReturnListOfUsers() {
        // Simula una lista de usuarios en el repositorio
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        // Simula la conversión de entidades a DTOs
        when(userMapper.toResponseList(anyList())).thenReturn(Arrays.asList(userResponse));

        // Llama al método del servicio
        List<UserResponse> result = userService.findAllUsers();

        // Verifica los resultados
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getName());
    }

    /**
     * Prueba que el método registerNewUser() registre correctamente un nuevo usuario
     * y que el campo hasFines quede en false por defecto.
     */
    @Test
    void registerNewUser_shouldSaveUserCorrectly() {
        // Simula el proceso de conversión y guardado
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Llama al método del servicio
        UserResponse response = userService.registerNewUser(userRequest);

        // Verifica los resultados
        assertNotNull(response);
        assertEquals("Juan", response.getName());
        assertFalse(response.getHasFines());

        // Verifica que se haya guardado el usuario
        verify(userRepository).save(user);
    }

    /**
     * Prueba que el método hasFines() devuelva true cuando el usuario tiene multas.
     */
    @Test
    void hasFines_shouldReturnTrue_whenUserHasFines() {
        user.setHasFines(true);
        assertTrue(userService.hasFines(user));
    }

    /**
     * Prueba que el método hasFines() devuelva false cuando el usuario no tiene multas.
     */
    @Test
    void hasFines_shouldReturnFalse_whenUserHasNoFines() {
        user.setHasFines(false);
        assertFalse(userService.hasFines(user));
    }

    /**
     * Prueba que el método addFines() marque correctamente a un usuario con multas.
     */
    @Test
    void addFines_shouldSetUserAsHavingFines() {
        // Configura el usuario sin multas
        user.setHasFines(false);

        // Simula que se encuentra el usuario en la base de datos
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Simula el guardado y conversión del usuario actualizado
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Ejecuta el método
        userService.addFines(1L);

        // Verifica que ahora tenga multas
        assertTrue(user.getHasFines());

        // Verifica que se haya guardado el usuario actualizado
        verify(userRepository).save(user);
    }

    /**
     * Prueba que el método clearFines() elimine correctamente las multas del usuario.
     */
    @Test
    void clearFines_shouldSetUserAsWithoutFines() {
        // Configura el usuario con multas
        user.setHasFines(true);

        // Simula que se encuentra y guarda correctamente
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Ejecuta el método
        userService.clearFines(1L);

        // Verifica que ya no tenga multas
        assertFalse(user.getHasFines());
        verify(userRepository).save(user);
    }

    /**
     * Prueba que el método isEligibleForLoan() devuelva true
     * cuando el usuario no tiene multas (puede prestar libros).
     */
    @Test
    void isEligibleForLoan_shouldReturnTrue_whenUserHasNoFines() {
        user.setHasFines(false);
        assertTrue(userService.isEligibleForLoan(user));
    }

    /**
     * Prueba que el método isEligibleForLoan() devuelva false
     * cuando el usuario tiene multas (no puede prestar libros).
     */
    @Test
    void isEligibleForLoan_shouldReturnFalse_whenUserHasFines() {
        user.setHasFines(true);
        assertFalse(userService.isEligibleForLoan(user));
    }
}
