package co.edu.uniremington.library.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for creating/updating a User.
 */
@Schema(description = "Datos para crear un nuevo usuario")
public class UserRequest {

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez", required = true)
    private String name;

    @Schema(description = "Email del usuario", example = "juan.perez@email.com", required = true)
    private String email;

    // Constructors
    public UserRequest() {
    }

    public UserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
