package co.edu.uniremington.products.service;

import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
import co.edu.uniremington.products.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("2500.00"));
    }


    @Test
    void shouldCreateProductSuccessfully() {
        when(productRepository.save(product1)).thenReturn(product1);

        Product saved = productService.createProduct(product1);

        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
        verify(productRepository).save(product1);
    }

    @Test
    void shouldFindProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product found = productService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(productRepository).findById(1L);
    }





}


