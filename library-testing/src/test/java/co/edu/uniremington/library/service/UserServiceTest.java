package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.UserRepository;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import co.edu.uniremington.library.service.mapper.UserMapper;
import org.h2.command.dml.MergeUsing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*; // Averiguar sobre esto


import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    private User user;
    private UserResponse userResponse;
    private UserRequest userRequest;



    @BeforeEach
    void setUp() {
        //Entity
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("prueba@email.com");
        user.setHasFines(false);

        //DTO
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("John Doe");
        userResponse.setEmail("prueba.email.com");
        userResponse.setHasFines(false);

    }

    @Test
    @DisplayName("Con esto queremos probar la funcionalidad de buscar un usuario por ID")
    void findById() {
        //Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        //When
        UserResponse userResponse = userService.findById(1L);

        //Then
        assertNotNull(userResponse);
        assertEquals("John Doe", userResponse.getName());
        assertEquals("prueba.email.com", userResponse.getEmail());
        Assertions.assertFalse(userResponse.getHasFines());
    }

    @Test
    @DisplayName("Prueba de consulta de todos los usuarios.")
    void findAllUsers() {
        //Given
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseList(List.of(user)).
    }

    @Test
    void registerNewUser() {
    }

    @Test
    void findEntityById() {
    }

    @Test
    void hasFines() {
    }

    @Test
    void addFines() {
    }

    @Test
    void clearFines() {
    }

    @Test
    void isEligibleForLoan() {
    }
}