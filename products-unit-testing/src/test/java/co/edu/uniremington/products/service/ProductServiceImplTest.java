package co.edu.uniremington.products.service;

import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
import co.edu.uniremington.products.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTING EXAM
 *
 * Implement unit tests for ProductServiceImpl
 * Using Mockito to mock ProductRepository
 * Cover the following scenarios:
 *  - createProduct: successful case
 *  - findById: successful case and when product not found
 *  - findAll: verify repository is called
 *  - updatePrice: successful case and when product not found
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
        product2.setPrice(new BigDecimal("100.00"));
    }

    // createProduct - caso exitoso
    @Test
    void shouldCreateProductSuccessfully() {
        when(productRepository.save(product1)).thenReturn(product1);

        Product saved = productService.createProduct(product1);

        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
        verify(productRepository).save(product1);
    }

    // findById - caso exitoso
    @Test
    void shouldFindProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product found = productService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(productRepository).findById(1L);
    }

    // findById - producto no encontrado
    @Test
    void shouldReturnNullWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Product found = productService.findById(99L);

        assertNull(found);
        verify(productRepository).findById(99L);
    }

    //  findAll - verificar que se llama al repositorio
    @Test
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository).findAll();
    }

    //  updatePrice - caso exitoso
    @Test
    void shouldUpdatePriceSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product updated = productService.updatePrice(1L, new BigDecimal("3000.00"));

        assertNotNull(updated);
        assertEquals(new BigDecimal("3000.00"), updated.getPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    //  updatePrice - producto no encontrado
    @Test
    void shouldReturnNullWhenUpdatingNonExistingProduct() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Product updated = productService.updatePrice(99L, new BigDecimal("5000.00"));

        assertNull(updated);
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any());
    }
}








