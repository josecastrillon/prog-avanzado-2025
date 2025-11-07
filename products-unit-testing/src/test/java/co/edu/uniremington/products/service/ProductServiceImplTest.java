package co.edu.uniremington.products.service.impl;

import co.edu.uniremington.products.exception.InvalidPriceException;
import co.edu.uniremington.products.exception.ProductNotFoundException;
import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        Product product = new Product(null, "Laptop", "Gaming Laptop", new BigDecimal("1200"), 10);
        Product savedProduct = new Product(1L, "Laptop", "Gaming Laptop", new BigDecimal("1200"), 10);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsNull() {
        Product product = new Product(null, null, "Desc", new BigDecimal("100"), 5);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                productService.createProduct(product)
        );

        assertEquals("Product name is required", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsInvalid() {
        Product product = new Product(null, "Mouse", "Wireless", new BigDecimal("-50"), 5);

        InvalidPriceException exception = assertThrows(InvalidPriceException.class, () ->
                productService.createProduct(product)
        );

        assertEquals("Price must be greater than zero", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        Product product = new Product(1L, "Keyboard", "Mechanical", new BigDecimal("80.00"), 15);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals("Keyboard", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldFindAllProducts() {
        List<Product> products = List.of(
                new Product(1L, "Keyboard", "Mechanical", new BigDecimal("89.99"), 10),
                new Product(2L, "Mouse", "Wireless", new BigDecimal("49.99"), 15)
        );

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdateProductPriceSuccessfully() {
        Product product = new Product(1L, "Keyboard", "Mechanical", new BigDecimal("80.00"), 10);
        Product updated = new Product(1L, "Keyboard", "Mechanical", new BigDecimal("100.00"), 10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        Product result = productService.updatePrice(1L, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), result.getPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidPrice() {
        assertThrows(InvalidPriceException.class, () ->
                productService.updatePrice(1L, new BigDecimal("-10"))
        );

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }
}
