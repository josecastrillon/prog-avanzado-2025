package co.edu.uniremington.products.controller;

import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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


    private Product response1;

    @BeforeEach
    void setUp() {

        response1 = new Product();
        response1.setId(1L);
        response1.setName("Clean Code");
        response1.setPrice(new BigDecimal("2500.00"));


    }

    @Test
    void shouldCreateProductAndReturnStatus201() throws Exception {

        when(productService.createProduct(any(Product.class))).thenReturn(response1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Clean Code\", \"price\": 2500.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Clean Code"))
                .andExpect(jsonPath("$.price").value(2500.00));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void GetProductById() throws Exception {
        when(productService.findById(1L)).thenReturn(response1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Clean Code"))
                .andExpect(jsonPath("$.price").value(2500.00));

        verify(productService).findById(1L);
    }

    @Test
    void ListAllProducts() throws Exception {
        when(productService.findAll()).thenReturn(List.of(response1));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Clean Code"));

        verify(productService).findAll();
    }

    @Test
    void UpdateProductPrice() throws Exception {
        Product updatedResponse = new Product();
        updatedResponse.setId(1L);
        updatedResponse.setName("Clean Code");
        updatedResponse.setPrice(new BigDecimal("2700.00"));

        when(productService.updatePrice(1L, new BigDecimal("2700.00"))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/products/1/price")
                        .param("price", "2700.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Clean Code"))
                .andExpect(jsonPath("$.price").value(2700.00));

        verify(productService).updatePrice(1L, new BigDecimal("2700.00"));
    }


}
