package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.UserNotFoundException;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.UserRepository;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import co.edu.uniremington.library.service.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
//given=arrage(organizar)
//when=actuar
//then=assert(afirmar)

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository; //lo usar pero no transformar

    @Mock
    private UserMapper userMapper; //lo usar pero no transformar

    @InjectMocks
    private UserService userService;  //lo estoy probando, se le inyectan los dos objetos

    private User user;  // entidad interna
    private UserResponse userResponse; // DTO de salida
    private UserRequest userRequest;

    @BeforeEach //antes de cada test cargar esta informacion
    void setUp() {
        // === Entidad interna ===
        user = new User();
        user.setId(1L);
        user.setName("Daniel");
        user.setEmail("daniel@example.com");
        user.setHasFines(false);

        // === DTO de entrada (lo que llega del cliente) ===
        userRequest = new UserRequest();
        userRequest.setName("Karen");
        userRequest.setEmail("karen@example.com");

        // === DTO de salida (lo que devuelve el servicio) ===
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Karen");
        userResponse.setEmail("karen@example.com");
        userResponse.setHasFines(false);
    }

    @Test
    @DisplayName("encontrar usuario por ID - éxito")
    void findById_success() {
        //given preparo
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        //whne lo pruebo
        UserResponse result = userService.findById(1L);

        //then lo afirmo
        Assertions.assertNotNull(result); //verifico que la informacion no este nula
        //verify(userRepository).findById(1L);
        //verify(userMapper).toResponse(user); opcionales
        Assertions.assertEquals("Karen", result.getName());
        Assertions.assertEquals("karen@example.com", result.getEmail());
    }

    @Test
    void findById_notFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseList(List.of(user))).thenReturn(List.of(userResponse));

        List<UserResponse> result = userService.findAllUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void registerNewUser_success() {
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.registerNewUser(userRequest);

        assertNotNull(result);
        assertFalse(user.getHasFines());
        verify(userRepository).save(user);
    }

    @Test
    void findEntityById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findEntityById(1L);

        assertEquals(user, result);
    }

    @Test
    void findEntityById_notFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findEntityById(1L));
    }

    @Test
    void hasFines_trueWhenUserHasFines() {

        user.setHasFines(true);
        assertTrue(userService.hasFines(user));
    }

    @Test
    void hasFines_falseWhenUserHasNoFines() {
        user.setHasFines(false);
        assertFalse(userService.hasFines(user));
    }

    @Test
    void hasFines_falseWhenNull() {
        user.setHasFines(null);
        assertFalse(userService.hasFines(user));
    }

    @Test
    void addFines_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.addFines(1L);

        assertNotNull(result);
        assertTrue(user.getHasFines());
        verify(userRepository).save(user);
    }

    @Test
    void clearFines_success() {
        user.setHasFines(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.clearFines(1L);

        assertNotNull(result);
        assertFalse(user.getHasFines());
        verify(userRepository).save(user);
    }

    @Test
    void isEligibleForLoan_trueWhenNoFines() {
        user.setHasFines(false);
        assertTrue(userService.isEligibleForLoan(user));
    }

    @Test
    void isEligibleForLoan_falseWhenHasFines() {
        user.setHasFines(true);
        assertFalse(userService.isEligibleForLoan(user));
    }
}
