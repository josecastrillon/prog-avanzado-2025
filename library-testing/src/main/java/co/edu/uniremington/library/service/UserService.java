package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.UserNotFoundException;
import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.repository.UserRepository;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import co.edu.uniremington.library.service.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for User-related operations.
 */
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    /**
     * Finds a user by ID and returns it as DTO.
     *
     * @param userId ID of the user
     * @return UserResponse DTO
     * @throws UserNotFoundException if user doesn't exist
     */
    public UserResponse findById(Long userId) {
        User user = findEntityById(userId);
        return userMapper.toResponse(user);
    }

    /**
     * Gets all users as DTOs.
     *
     * @return List of UserResponse DTOs
     */
    public List<UserResponse> findAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseList(users);
    }

    /**
     * Registers a new user in the system.
     *
     * @param request UserRequest DTO from controller
     * @return UserResponse DTO with saved data (including generated ID)
     */
    public UserResponse registerNewUser(UserRequest request) {
        // Convert DTO to Entity
        User user = userMapper.toEntity(request);

        // Ensure new users don't have fines (business logic)
        if (user.getHasFines() == null) {
            user.setHasFines(false);
        }

        // Save and return as DTO
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Finds a user entity by ID.
     *
     * @param userId ID of the user
     * @return User entity
     * @throws UserNotFoundException if user doesn't exist
     */
    User findEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Checks if a user has pending fines.
     *
     * @param user User to check
     * @return true if user has fines
     */
    public boolean hasFines(User user) {
        return user.getHasFines() != null && user.getHasFines();
    }


    /**
     * Marks a user as having fines.
     *
     * @param userId ID of the user
     * @return UserResponse DTO with updated fines status
     */
    public UserResponse addFines(Long userId) {
        User user = findEntityById(userId);  // Use internal method to get entity
        user.setHasFines(true);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Clears the fines for a user.
     *
     * @param userId ID of the user
     * @return UserResponse DTO with updated fines status
     */
    public UserResponse clearFines(Long userId) {
        User user = findEntityById(userId);  // Use internal method to get entity
        user.setHasFines(false);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Checks if a user is eligible to loan books.
     *
     * @param user User to check
     * @return true if user can loan books
     */
    public boolean isEligibleForLoan(User user) {
        return !hasFines(user);
    }
}
