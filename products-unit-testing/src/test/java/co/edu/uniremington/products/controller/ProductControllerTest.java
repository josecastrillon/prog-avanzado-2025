package co.edu.uniremington.products.controller;

import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * UNIT TESTING EXAM
 *
 * Instructions:
 * 1. Implement unit tests for ProductController
 * 2. Use Mockito to mock the ProductService
 * 3. Cover the following scenarios:
 *    - POST /api/products: successful creation
 *    - GET /api/products/{id}: get existing product
 *    - GET /api/products: list all products
 *    - PATCH /api/products/{id}/price: update price
 *
 * Evaluation criteria:
 * - Correct use of mocks
 * - Verification of HTTP status codes
 * - Verification of responses
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // --- Product 1 ---
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("2500.00"));

        // --- Product 2 ---
        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setPrice(new BigDecimal("50.00"));
    }



    //Create Product
    //Crear producto exitosamente
    @Test
    @DisplayName("POST /api/products - Cuando se crea un producto válido, retorna HTTP 201 y los datos del producto")
    void createProduct_whenValidProduct_thenReturnHttp201() throws Exception {
        // Given
        when(productService.createProduct(any(Product.class))).thenReturn(product1);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(2500.00)));

        verify(productService).createProduct(any(Product.class));
    }
    //FindById
    //Buscar producto por ID (caso exitoso)
    @Test
    @DisplayName("GET /api/products/{id} - Cuando el producto existe, retorna HTTP 200 con el producto")
    void findById_whenProductExists_thenReturnHttp200() throws Exception {
        when(productService.findById(1L)).thenReturn(product1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(2500.00)));

        verify(productService).findById(1L);
    }


    // TODO: Implement your tests here

    @Test
    void shouldCreateProductAndReturnStatus201() {
        // TODO: Implement
    }
}
