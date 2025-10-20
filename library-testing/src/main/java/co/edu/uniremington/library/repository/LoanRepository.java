package co.edu.uniremington.library.repository;

import co.edu.uniremington.library.domain.model.Book;
import co.edu.uniremington.library.domain.model.Loan;
import co.edu.uniremington.library.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserAndActiveTrue(User user);
    Optional<Loan> findByBookAndUserAndActiveTrue(Book book, User user);

    // New methods for separated services
    List<Loan> findByActive(Boolean active);
    List<Loan> findByUser(User user);
}
