package co.edu.uniremington.library.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for Book responses.
 */
@Schema(description = "Información de un libro")
public class BookResponse {

    @Schema(description = "ID único del libro", example = "1")
    private Long id;

    @Schema(description = "Título del libro", example = "Clean Code")
    private String title;

    @Schema(description = "Autor del libro", example = "Robert C. Martin")
    private String author;

    @Schema(description = "ISBN del libro", example = "978-0132350884")
    private String isbn;

    @Schema(description = "Copias totales del libro", example = "5")
    private Integer totalCopies;

    @Schema(description = "Copias disponibles para préstamo", example = "3")
    private Integer availableCopies;

    // Constructors
    public BookResponse() {
    }

    public BookResponse(Long id, String title, String author, String isbn, Integer totalCopies, Integer availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
