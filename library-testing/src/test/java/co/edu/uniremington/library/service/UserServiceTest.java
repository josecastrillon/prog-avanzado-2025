package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.UserNotFoundException;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.UserRepository;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import co.edu.uniremington.library.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserService}.
 */
class UserServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void findById_userExists_returnsUserResponse() {
        User user = new User();
        user.setId(1L);
        UserResponse expectedResponse = new UserResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    void findById_userNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void findAllUsers_returnsListOfUsers() {
        User user1 = new User();
        User user2 = new User();
        List<User> users = List.of(user1, user2);
        List<UserResponse> expectedResponses = List.of(new UserResponse(), new UserResponse());

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(expectedResponses);

        List<UserResponse> result = userService.findAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
        verify(userMapper).toResponseList(users);
    }

    @Test
    void registerNewUser_createsUserSuccessfully() {
        UserRequest request = new UserRequest();
        User user = new User();
        user.setHasFines(null);
        User savedUser = new User();
        savedUser.setHasFines(false);
        UserResponse expectedResponse = new UserResponse();

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.registerNewUser(request);

        assertNotNull(result);
        assertFalse(savedUser.getHasFines());
        verify(userRepository).save(user);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void findEntityById_userFound_returnsEntity() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.findEntityById(1L);
        assertEquals(user, result);
    }

    @Test
    void findEntityById_userNotFound_throwsException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findEntityById(2L));
    }

    @Test
    void hasFines_returnsTrueIfUserHasFines() {
        User user = new User();
        user.setHasFines(true);
        assertTrue(userService.hasFines(user));
    }

    @Test
    void hasFines_returnsFalseIfUserHasNoFines() {
        User user = new User();
        user.setHasFines(false);
        assertFalse(userService.hasFines(user));
    }

    @Test
    void addFines_setsHasFinesToTrue() {
        User user = new User();
        user.setId(1L);
        user.setHasFines(false);
        User updatedUser = new User();
        updatedUser.setHasFines(true);
        UserResponse expectedResponse = new UserResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.addFines(1L);

        assertNotNull(result);
        assertTrue(updatedUser.getHasFines());
        verify(userRepository).save(user);
    }

    @Test
    void clearFines_setsHasFinesToFalse() {
        User user = new User();
        user.setId(1L);
        user.setHasFines(true);
        User updatedUser = new User();
        updatedUser.setHasFines(false);
        UserResponse expectedResponse = new UserResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.clearFines(1L);

        assertNotNull(result);
        assertFalse(updatedUser.getHasFines());
        verify(userRepository).save(user);
    }

    @Test
    void isEligibleForLoan_returnsTrueWhenNoFines() {
        User user = new User();
        user.setHasFines(false);
        assertTrue(userService.isEligibleForLoan(user));
    }

    @Test
    void isEligibleForLoan_returnsFalseWhenHasFines() {
        User user = new User();
        user.setHasFines(true);
        assertFalse(userService.isEligibleForLoan(user));
    }
}
