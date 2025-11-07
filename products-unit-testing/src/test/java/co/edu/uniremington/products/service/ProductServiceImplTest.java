package co.edu.uniremington.products.service;

import co.edu.uniremington.products.exception.InvalidPriceException;
import co.edu.uniremington.products.exception.ProductNotFoundException;
import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
import co.edu.uniremington.products.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTING EXAM
 *
 * Instructions:
 * 1. Implement unit tests for ProductServiceImpl
 * 2. Use Mockito to mock the ProductRepository
 * 3. Cover the following scenarios:
 *    - createProduct: successful case and validations
 *    - findById: successful case and when product not found
 *    - findAll: verify repository is called
 *    - updatePrice: successful case and validations
 *
 * Evaluation criteria:
 * - Correct use of @Mock, @InjectMocks
 * - Use of when().thenReturn() and verify()
 * - Appropriate assertions
 * - Exception handling
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;


    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("2500.00"));

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setPrice(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("/api/products")
    void POST_(){
        //Given
        when(productService);
    }


    // TODO: Implement your tests here

    @Test
    void shouldCreateProductSuccessfully() {
        // TODO: Implement
    }
}
