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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class) //Activa el mockito
class UserServiceTest {

    //Dependencias
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    //Objeto de prueba
    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {

        // Setup entity (used internally by service)
        user = new User();
        user.setId(1L);
        user.setName("Maria Bueno");
        user.setEmail("bvmafe@gmail.com");
        user.setHasFines(false);

        // Setup DTOs
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Maria Bueno");
        userResponse.setEmail("bvmafe@gmail.com");
        userResponse.setHasFines(false);

        userRequest = new UserRequest();
        userRequest.setName("Maria Bueno");
        userRequest.setEmail("bvmafe@gmail.com");
        userRequest.setHasFines(false);

    }

    // =====================================================
    // TESTS: findById
    // =====================================================
    @Test
    //Busca el ususario
    @DisplayName("Cuando se encuentran todos los usuarios, se devuelven todos los DTO UserResponse.")
    void whenFindingUserById_thenUserResponseReturned() {
        //Given
        //Va al repositorio y va a traer el finbyid
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        //When
        UserResponse result = userService.findById(1L);

        //Then
        assertNotNull(result); //Que no sea null
        assertEquals("Maria Bueno", result.getName()); // Verificar que nos trae el nombre
        assertEquals("bvmafe@gmail.com", result.getEmail()); // Verifica la informacion del email
        assertFalse(userResponse.getHasFines());

        //Verify
        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    // =====================================================
    // TESTS: findAllUsers
    // =====================================================
    @Test
    //Devuelve la lista de usuario
    @DisplayName("Cuando se encuentran todos los usuarios, se devuelven todos los DTO UserResponse.")
    void whenFindingAllUsers_thenUserResponsesReturned() {
        //Given
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseList(List.of(user))).thenReturn(List.of(userResponse));

        //When
        List<UserResponse> result = userService.findAllUsers();

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Maria Bueno", result.get(0).getName());

        //Verify
        verify(userRepository).findAll();
        verify(userMapper).toResponseList(List.of(user));
    }

    // =====================================================
    // TESTS: UserByInvalid
    // =====================================================

    @Test
    //Simula que el repositorio no encuentra nada
    @DisplayName("Cuando se encuentra un usuario con un ID no válido, se produce una excepción.")
    void whenFindingUserByInvalid_thenTrowsException() {
        //Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));

        //verify
        verify(userRepository).findById(99L);
    }

    // =====================================================
    // TESTS: RegisteringNewUser
    // =====================================================
    @Test
    //Verifica que el usuario se guarde y devuelva su informaacion
    @DisplayName("Al registrar un nuevo usuario, se devuelve UserResponse.")
    void whenRegisteringNewUser_thenUserResponseReturned() {
        //Given
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        //When
        UserResponse result = userService.registerNewUser(userRequest);

        //Then
        assertNotNull(result);
        assertEquals("Maria Bueno", result.getName());
        assertFalse(result.getHasFines());

        //Verify
        verify(userMapper).toEntity(userRequest);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    // =====================================================
    // TESTS: AddingFines
    // =====================================================
    @Test
    //Simula que se le agregan multas al usuario
    @DisplayName("Cuando se añaden multas al usuario, UserResponse muestra hasFines=true.")
    void whenAddingFines_thenUserHasFinesTrue() {
        //Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setHasFines(true);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        //When
        UserResponse result = userService.addFines(1L);

        //Then
        assertNotNull(result);
        assertEquals("Maria Bueno", result.getName());

        //Verify
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);

    }

    // =====================================================
    // TESTS: ClearingFines
    // =====================================================
    @Test
    //Marca el usuario que tenga multas
    @DisplayName("Cuando se liquidan las multas, UserResponse muestra hasFines=false.")
    void whenClearingFines_thenUserHasFinesFalse() {
        //Given
        user.setHasFines(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setHasFines(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        //When
        UserResponse result = userService.clearFines(1L);

        //Then
        assertNotNull(result);
        assertEquals("Maria Bueno", result.getName());
        assertFalse(result.getHasFines());

        //Verify
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    // =====================================================
    // TESTS: CheckingEligibility
    // =====================================================
    @Test
    //Busca el usuario sin multas
    @DisplayName("Al verificar la elegibilidad para el préstamo, devuelve verdadero si no hay multas.")
    void whenCheckingEligibility_thenReturnsTrueIfNoFines() {
        //When
        boolean result = userService.isEligibleForLoan(user);

        //Then
        assertTrue(result);
    }

    // =====================================================
    // TESTS: CheckingEligibility
    // =====================================================
    @Test
    //Busca el usuario con multas
    @DisplayName("Cuando Al verificar la elegibilidad para el préstamo, devuelve falso si el usuario tiene multas.")
    void whenCheckingEligibility_thenReturnsFalseIfHasFines() {
        //Given
        user.setHasFines(true);

        //When
        boolean result = userService.isEligibleForLoan((user));

        //Then
        assertFalse(result);
    }
}