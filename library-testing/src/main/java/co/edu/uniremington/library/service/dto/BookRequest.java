package co.edu.uniremington.library.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for creating/updating a Book.
 */
@Schema(description = "Datos para crear o actualizar un libro")
public class BookRequest {

    @Schema(description = "Título del libro", example = "Clean Code", required = true)
    private String title;

    @Schema(description = "Autor del libro", example = "Robert C. Martin", required = true)
    private String author;

    @Schema(description = "ISBN del libro", example = "978-0132350884")
    private String isbn;

    @Schema(description = "Copias totales del libro", example = "5", minimum = "1")
    private Integer totalCopies;

    @Schema(description = "Copias disponibles para préstamo", example = "5", minimum = "0")
    private Integer availableCopies;

    // Constructors
    public BookRequest() {
    }

    public BookRequest(String title, String author, String isbn, Integer totalCopies, Integer availableCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }
}
