package co.edu.uniremington.library.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for User responses.
 */
@Schema(description = "Información de un usuario")
public class    UserResponse {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String name;

    @Schema(description = "Email del usuario", example = "juan.perez@email.com")
    private String email;

    @Schema(description = "Indica si el usuario tiene multas pendientes", example = "false")
    private Boolean hasFines;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email, Boolean hasFines) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hasFines = hasFines;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getHasFines() {
        return hasFines;
    }

    public void setHasFines(Boolean hasFines) {
        this.hasFines = hasFines;
    }
}
