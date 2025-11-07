package co.edu.uniremington.products.controller;

import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // para convertir objetos a JSON

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        // Arrange
        Product product = new Product(1L, "Keyboard", "Mechanical Keyboard",
                new BigDecimal("89.99"), 15);

        Mockito.when(productService.createProduct(any(Product.class)))
                .thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(89.99))
                .andExpect(jsonPath("$.stock").value(15));
    }

    @Test
    void shouldGetProductById() throws Exception {
        // Arrange
        Product product = new Product(1L, "Mouse", "Wireless Mouse",
                new BigDecimal("49.99"), 20);

        Mockito.when(productService.findById(1L)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.price").value(49.99))
                .andExpect(jsonPath("$.stock").value(20));
    }

    @Test
    void shouldListAllProducts() throws Exception {
        // Arrange
        List<Product> products = List.of(
                new Product(1L, "Keyboard", "Mechanical", new BigDecimal("89.99"), 15),
                new Product(2L, "Mouse", "Wireless", new BigDecimal("49.99"), 20)
        );

        Mockito.when(productService.findAll()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Keyboard"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
    }

    @Test
    void shouldUpdateProductPrice() throws Exception {
        // Arrange
        Product updated = new Product(1L, "Keyboard", "Mechanical",
                new BigDecimal("99.99"), 15);

        Mockito.when(productService.updatePrice(1L, new BigDecimal("99.99")))
                .thenReturn(updated);

        // Act & Assert
        mockMvc.perform(patch("/api/products/1/price")
                        .param("price", "99.99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.name").value("Keyboard"));
    }
}
