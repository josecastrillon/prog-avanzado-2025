package co.edu.uniremington.library.service.mapper;

import co.edu.uniremington.library.domain.model.Loan;
import co.edu.uniremington.library.service.dto.LoanResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for converting between Loan entities and DTOs.
 */
@Component
public class LoanMapper {

    /**
     * Converts a Loan entity to a LoanResponse DTO.
     * <p>
     * This method extracts only the data needed for the API response,
     * hiding internal entity details from the client.
     *
     * @param loan The Loan entity from the database
     * @return LoanResponse DTO ready to be sent to the client
     */
    public LoanResponse toResponse(Loan loan) {
        if (loan == null) {
            return null;
        }

        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setBookTitle(loan.getBook().getTitle());
        response.setUserName(loan.getUser().getName());
        response.setLoanDate(loan.getLoanDate());
        response.setReturnDate(loan.getReturnDate());
        response.setActive(loan.getActive());

        return response;
    }

    /**
     * Converts a list of Loan entities to a list of LoanResponse DTOs.
     * <p>
     * Useful for endpoints that return multiple loans.
     * Uses Java Streams for functional-style transformation.
     *
     * @param loans List of Loan entities
     * @return List of LoanResponse DTOs
     */
    public List<LoanResponse> toResponseList(List<Loan> loans) {
        if (loans == null) {
            return null;
        }

        return loans.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
