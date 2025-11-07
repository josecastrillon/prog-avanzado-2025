package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.service.UserService;
import co.edu.uniremington.library.service.dto.UserRequest;
import co.edu.uniremington.library.service.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User operations.
 *
 *
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API para gestión de usuarios de la biblioteca")
public class UserController {
    private final UserService userService;

    /**
     * Constructor with dependency injection.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets all users.
     *
     * @return List of all users as DTOs
     */
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Devuelve una lista completa de todos los usuarios registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        // Service handles everything: entities, mapping, business logic
        return ResponseEntity.ok(userService.findAllUsers());
    }

    /**
     * Gets a user by ID.
     *
     * @param id User ID
     * @return User details as DTO
     */
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Busca y devuelve un usuario específico por su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID del usuario a buscar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Registers a new user.
     *
     * @param request User data from client
     * @return Registered user as DTO
     */
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema. El campo hasFines se inicializa automáticamente en false."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos")
    })
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true
            )
            @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerNewUser(request));
    }



    /**
     * Adds fines to a user.
     *
     * @param id User ID
     * @return Updated user as DTO
     */
    @Operation(
            summary = "Agregar multas a usuario",
            description = "Marca al usuario como que tiene multas pendientes. " +
                    "Un usuario con multas no puede realizar nuevos préstamos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multas agregadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/{id}/fines")
    public ResponseEntity<UserResponse> addFines(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.addFines(id));
    }

    /**
     * Clears fines for a user.
     *
     * @param id User ID
     * @return Updated user as DTO
     */
    @Operation(
            summary = "Limpiar multas de usuario",
            description = "Remueve las multas del usuario, permitiéndole realizar nuevos préstamos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multas removidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}/fines")
    public ResponseEntity<UserResponse> clearFines(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.clearFines(id));
    }
}