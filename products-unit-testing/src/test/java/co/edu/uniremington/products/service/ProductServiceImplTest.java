package co.edu.uniremington.products.service;

import co.edu.uniremington.products.exception.InvalidPriceException;
import co.edu.uniremington.products.exception.ProductNotFoundException;
import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
import co.edu.uniremington.products.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High-end gaming laptop");
        product.setPrice(new BigDecimal("2500.00"));
        product.setStock(5);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.createProduct(product);

        assertNotNull(savedProduct);
        assertEquals("Laptop", savedProduct.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsMissing() {
        product.setName("  ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(product)
        );

        assertEquals("Product name is required", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowInvalidPriceExceptionWhenPriceIsZero() {
        product.setPrice(BigDecimal.ZERO);

        assertThrows(InvalidPriceException.class, () -> productService.createProduct(product));
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product found = productService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("Laptop", found.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenIdDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(99L));
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void shouldReturnAllProductsSuccessfully() {
        Product secondProduct = new Product(2L, "Mouse", "Wireless mouse",
                new BigDecimal("100.00"), 15);

        when(productRepository.findAll()).thenReturn(List.of(product, secondProduct));

        List<Product> products = productService.findAll();

        assertEquals(2, products.size());
        assertTrue(products.contains(product));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdateProductPriceSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updatePrice(1L, new BigDecimal("3000.00"));

        assertEquals(new BigDecimal("3000.00"), updatedProduct.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldThrowInvalidPriceExceptionWhenUpdatingWithNegativePrice() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InvalidPriceException.class, () -> productService.updatePrice(1L, new BigDecimal("-100.00")));
        verify(productRepository, never()).save(any());
    }
}
