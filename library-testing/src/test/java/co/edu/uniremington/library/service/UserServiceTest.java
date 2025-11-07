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

/**
 * Unit tests for UserService.
 */
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
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Angelo");
        user.setEmail("angelo@example.com");
        user.setHasFines(false);

        userRequest = new UserRequest("Salomé", "salome@example.com");

        userResponse = new UserResponse(1L, "Angelo", "angelo@example.com", false);
    }

    @Test
    void findById_ShouldReturnUserResponse_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("Angelo", result.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    void findById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void findAllUsers_ShouldReturnListOfUserResponses() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userMapper.toResponseList(anyList())).thenReturn(Arrays.asList(userResponse));

        List<UserResponse> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toResponseList(anyList());
    }

    @Test
    void registerNewUser_ShouldSaveUserAndReturnResponse() {
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.registerNewUser(userRequest);

        assertNotNull(result);
        assertEquals("Angelo", result.getName());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toEntity(userRequest);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    void findEntityById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findEntityById(1L);

        assertNotNull(result);
        assertEquals("Angelo", result.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findEntityById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findEntityById(10L));
        verify(userRepository, times(1)).findById(10L);
    }

    @Test
    void hasFines_ShouldReturnFalse_WhenUserHasNoFines() {
        user.setHasFines(false);

        boolean result = userService.hasFines(user);

        assertFalse(result);
    }

    @Test
    void hasFines_ShouldReturnTrue_WhenUserHasFines() {
        user.setHasFines(true);

        boolean result = userService.hasFines(user);

        assertTrue(result);
    }

    @Test
    void addFines_ShouldSetFinesToTrueAndReturnResponse() {
        user.setHasFines(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.addFines(1L);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void clearFines_ShouldSetFinesToFalseAndReturnResponse() {
        user.setHasFines(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.clearFines(1L);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void isEligibleForLoan_ShouldReturnTrue_WhenUserHasNoFines() {
        user.setHasFines(false);

        boolean result = userService.isEligibleForLoan(user);

        assertTrue(result);
    }

    @Test
    void isEligibleForLoan_ShouldReturnFalse_WhenUserHasFines() {
        user.setHasFines(true);

        boolean result = userService.isEligibleForLoan(user);

        assertFalse(result);
    }
}
