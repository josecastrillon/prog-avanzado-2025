package co.edu.uniremington.library.controller;

import co.edu.uniremington.library.service.BookService;
import co.edu.uniremington.library.service.dto.BookRequest;
import co.edu.uniremington.library.service.dto.BookResponse;
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
 * REST Controller for Book operations.
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "API para gestión de libros de la biblioteca")
public class BookController {
    private final BookService bookService;

    /**
     * Constructor with dependency injection.
     * <p>
     * NOTE: We inject ONLY the service.
     * - Service handles everything: business logic AND DTO↔Entity mapping
     * - Controller is a thin layer that just handles HTTP
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Gets all books.
     *
     * @return List of all books as DTOs
     */
    @Operation(
            summary = "Obtener todos los libros",
            description = "Devuelve una lista completa de todos los libros en el sistema, " +
                    "incluyendo los que no tienen copias disponibles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        // Service handles everything: entities, mapping, business logic
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    /**
     * Gets all available books (with at least one copy available).
     *
     * @return List of available books as DTOs
     */
    @Operation(
            summary = "Obtener libros disponibles",
            description = "Devuelve solo los libros que tienen al menos una copia disponible para préstamo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de libros disponibles obtenida exitosamente")
    })
    @GetMapping("/available")
    public ResponseEntity<List<BookResponse>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.findAvailableBooks());
    }

    /**
     * Gets a book by ID.
     *
     * @param id Book ID
     * @return Book details as DTO
     */
    @Operation(
            summary = "Obtener libro por ID",
            description = "Busca y devuelve un libro específico por su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "ID del libro a buscar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    /**
     * Registers a new book.
     * <p>
     * CLEAN ARCHITECTURE: Controller just passes DTO to Service
     * ===========================================================
     * The Service handles:
     * - Converting DTO to Entity (using mapper)
     * - Applying business rules
     * - Saving to database
     * - Converting Entity back to DTO
     *
     * @param request Book data from client
     * @return Registered book as DTO
     */
    @Operation(
            summary = "Registrar nuevo libro",
            description = "Crea un nuevo libro en el sistema. Si no se especifican las copias, " +
                    "se asignan valores por defecto (totalCopies=1, availableCopies=1)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del libro inválidos")
    })
    @PostMapping
    public ResponseEntity<BookResponse> registerBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo libro",
                    required = true
            )
            @RequestBody BookRequest request) {
        // Service handles ALL the complexity - controller just passes the DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.registerNewBook(request));
    }
}


