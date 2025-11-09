package co.edu.uniremington.products.service.impl;

import co.edu.uniremington.products.exception.InvalidPriceException;
import co.edu.uniremington.products.exception.ProductNotFoundException;
import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_success() {
        Product product = new Product(null, "Laptop", "HP", new BigDecimal("1500000"), 5);

        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_invalidName_shouldThrowException() {
        Product product = new Product(null, "", "HP", new BigDecimal("1500000"), 5);
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(product));
    }

    @Test
    void createProduct_invalidPrice_shouldThrowException() {
        Product product = new Product(null, "Monitor", "LCD", new BigDecimal("-10"), 3);
        assertThrows(InvalidPriceException.class, () -> productService.createProduct(product));
    }

    @Test
    void findById_success() {
        Product product = new Product(1L, "Mouse", "Óptico", new BigDecimal("30000"), 10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findById(1L);

        assertEquals(product, result);
        verify(productRepository).findById(1L);
    }

    @Test
    void findById_notFound_shouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.findById(99L));
    }

    @Test
    void findAll_success() {
        when(productRepository.findAll()).thenReturn(List.of());
        List<Product> result = productService.findAll();
        assertNotNull(result);
        verify(productRepository).findAll();
    }

    @Test
    void updatePrice_success() {
        Product product = new Product(1L, "Teclado", "Mecánico", new BigDecimal("50000"), 7);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product updated = productService.updatePrice(1L, new BigDecimal("60000"));

        assertEquals(new BigDecimal("60000"), updated.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    void updatePrice_invalidPrice_shouldThrowException() {
        assertThrows(InvalidPriceException.class,
                () -> productService.updatePrice(1L, new BigDecimal("-1")));
    }

    @Test
    void updatePrice_notFound_shouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,
                () -> productService.updatePrice(999L, new BigDecimal("50000")));
    }
}
