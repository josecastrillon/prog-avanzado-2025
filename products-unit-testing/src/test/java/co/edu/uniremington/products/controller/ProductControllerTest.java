package co.edu.uniremington.products.controller;

import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.service.ProductService;
import co.edu.uniremington.products.controller.ProductController; // Import importante

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void createProduct_shouldReturn201() {
        Product product = new Product(null, "Laptop", "HP", new BigDecimal("1200"), 10);

        when(productService.createProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(product, response.getBody());
        verify(productService).createProduct(product);
    }

    @Test
    void getProductById_shouldReturn200() {
        Product product = new Product(1L, "Mouse", "Wireless", new BigDecimal("50"), 20);

        when(productService.findById(1L)).thenReturn(product);

        ResponseEntity<Product> response = productController.getProduct(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(product, response.getBody());
        verify(productService).findById(1L);
    }

    @Test
    void listProducts_shouldReturn200() {
        List<Product> products = List.of(
                new Product(1L, "Keyboard", "RGB", new BigDecimal("150000"), 5),
                new Product(2L, "Monitor", "27 inch", new BigDecimal("800000"), 3)
        );

        when(productService.findAll()).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.listProducts();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(products, response.getBody());
        verify(productService).findAll();
    }

    @Test
    void updatePrice_shouldReturn200() {
        Product updatedProduct = new Product(1L, "Keyboard", "RGB", new BigDecimal("180000"), 5);

        when(productService.updatePrice(1L, new BigDecimal("180000")))
                .thenReturn(updatedProduct);

        ResponseEntity<Product> response =
                productController.updatePrice(1L, new BigDecimal("180000"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updatedProduct, response.getBody());
        verify(productService).updatePrice(1L, new BigDecimal("180000"));
    }
}
