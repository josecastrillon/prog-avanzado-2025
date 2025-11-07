package co.edu.uniremington.library.service;

import co.edu.uniremington.library.domain.exception.BookNotFoundException;
import co.edu.uniremington.library.domain.model.Book;
import co.edu.uniremington.library.repository.BookRepository;
import co.edu.uniremington.library.service.dto.BookRequest;
import co.edu.uniremington.library.service.dto.BookResponse;
import co.edu.uniremington.library.service.mapper.BookMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Book-related operations.
 */
@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Finds a book by ID and returns it as DTO.
     *
     * @param bookId ID of the book
     * @return BookResponse DTO
     * @throws BookNotFoundException if book doesn't exist
     */
    public BookResponse findById(Long bookId) {
        Book book = findEntityById(bookId);
        return bookMapper.toResponse(book);
    }

    /**
     * Gets all available books (with at least one copy available) as DTOs.
     *
     * @return List of BookResponse DTOs
     */
    public List<BookResponse> findAvailableBooks() {
        List<Book> books = bookRepository.findByAvailableCopiesGreaterThan(0);
        return bookMapper.toResponseList(books);
    }

    /**
     * Gets all books (regardless of availability) as DTOs.
     *
     * @return List of BookResponse DTOs
     */
    public List<BookResponse> findAllBooks() {
        List<Book> books = bookRepository.findAll();
        return bookMapper.toResponseList(books);
    }

    /**
     * Registers a new book in the system.
     *
     * @param request BookRequest DTO from controller
     * @return BookResponse DTO with saved data (including generated ID)
     */
    public BookResponse registerNewBook(BookRequest request) {
        // Convert DTO to Entity
        Book book = bookMapper.toEntity(request);

        // Apply default values for copies (business logic)
        if (book.getTotalCopies() == null) {
            book.setTotalCopies(1);
        }
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        // Save and return as DTO
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    /**
     * Finds a book entity by ID.
     *
     * @param bookId ID of the book
     * @return Book entity
     * @throws BookNotFoundException if book doesn't exist
     */
    Book findEntityById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));
    }

    /**
     * Decrements the available copies of a book (when loaning).
     *
     * @param book Book to update
     */
    void decrementAvailableCopies(Book book) {
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    /**
     * Increments the available copies of a book (when returning).
     *
     * @param book Book to update
     */
    void incrementAvailableCopies(Book book) {
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    /**
     * Checks if a book has available copies.
     *
     * @param book Book to check
     * @return true if book has at least one available copy
     */
    public boolean hasAvailableCopies(Book book) {
        return book.getAvailableCopies() > 0;
    }
}
