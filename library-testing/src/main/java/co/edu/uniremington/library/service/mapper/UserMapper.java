package co.edu.uniremington.library.service.mapper;

import co.edu.uniremington.library.domain.model.User;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for User entity and DTOs.
 */
@Component
public class UserMapper {

    /**
     * Converts UserRequest DTO to User entity.
     * <p>
     * NOTE: hasFines is NOT set from the request.
     * It will be initialized to false by UserService.registerNewUser()
     *
     * @param request DTO from client
     * @return User entity
     */
    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // hasFines intentionally NOT set here - managed separately

        return user;
    }

    /**
     * Converts User entity to UserResponse DTO.
     *
     * @param user Entity from database
     * @return DTO for client
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setHasFines(user.getHasFines());

        return response;
    }

    /**
     * Converts a list of User entities to UserResponse DTOs.
     *
     * @param users List of entities
     * @return List of DTOs
     */
    public List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing User entity with data from UserRequest.
     * <p>
     * NOTE: We DON'T update:
     * - ID (immutable)
     * - hasFines (managed through specific endpoints)
     *
     * @param user    Existing entity
     * @param request Updated data
     */
    public void updateEntityFromRequest(User user, UserRequest request) {
        if (user == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        // hasFines is NEVER updated from UserRequest
        // Use dedicated endpoints: POST/DELETE /api/users/{id}/fines
    }
}