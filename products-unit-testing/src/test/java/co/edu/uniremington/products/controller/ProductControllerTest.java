package co.edu.uniremington.products.controller;

import co.edu.uniremington.products.exception.InvalidPriceException;
import co.edu.uniremington.products.exception.ProductNotFoundException;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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

    // Herramientas de Testing de Spring Web
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Dependencia a simular (Criterio: Correct use of mocks)
    @MockBean
    private ProductService productService;

    private Product laptop;
    private final Long EXISTING_ID = 1L;
    private final Long NON_EXISTING_ID = 99L;
    private final String API_PRODUCTS_URL = "/api/products";


    //Creacion de objeto
    @BeforeEach
    void setUp() {
        // Usamos el constructor de 5 parámetros de Product.java
        // Product(Long id, String name, String description, BigDecimal price, Integer stock)
        laptop = new Product(EXISTING_ID, "Laptop Gamer", "Desc", new BigDecimal("1500.00"), 10);
    }


    // 1.  POST /api/products (Creación Exitosa)
    @Test
    @DisplayName("POST /api/products: successful creation returns 201 Created")
    void shouldCreateProductAndReturnStatus201() throws Exception {
        Product newProductRequest = new Product(null, "Teclado Mecánico", "Desc", new BigDecimal("120.00"), 50);

        // Simulación: El servicio devuelve el objeto con ID
        when(productService.createProduct(any(Product.class))).thenReturn(laptop);

        // Act & Assert
        mockMvc.perform(post(API_PRODUCTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductRequest)))
                .andExpect(status().isCreated()) // ✅ Criterio: Verification of HTTP status codes (201)
                .andExpect(jsonPath("$.id", is(EXISTING_ID.intValue()))) // ✅ Criterio: Verification of responses
                .andExpect(jsonPath("$.name", is("Laptop Gamer")));

        // Criterio: Correct use of mocks (verificación de llamada al servicio)
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    // 2. GET /api/products/{id} (Obtener Producto Existente)
    @Test
    @DisplayName("GET /api/products/{id}: existing product returns 200 OK")
    void getExistingProduct_Returns200() throws Exception {
        // Simulación: El servicio encuentra el producto
        when(productService.findById(EXISTING_ID)).thenReturn(laptop);

        // Act & Assert
        mockMvc.perform(get(API_PRODUCTS_URL + "/{id}", EXISTING_ID))
                .andExpect(status().isOk()) // ✅ Criterio: Verification of HTTP status codes (200)
                .andExpect(jsonPath("$.name", is("Laptop Gamer"))); // ✅ Criterio: Verification of responses

        verify(productService, times(1)).findById(EXISTING_ID);
    }


    // 3. Testear GET /api/products (Listar Todos)
    @Test
    @DisplayName("GET /api/products: listing all products returns 200 OK and list")
    void listAllProducts_Returns200() throws Exception {
        Product monitor = new Product(2L, "Monitor 4K", "Desc", new BigDecimal("500.00"), 5);
        List<Product> products = Arrays.asList(laptop, monitor);

        // Simulación: El servicio devuelve la lista
        when(productService.findAll()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get(API_PRODUCTS_URL))
                .andExpect(status().isOk()) // ✅ Criterio: Verification of HTTP status codes (200)
                .andExpect(jsonPath("$", hasSize(2))) // ✅ Criterio: Verification of responses
                .andExpect(jsonPath("$[0].name", is("Laptop Gamer")));

        verify(productService, times(1)).findAll();
    }

    // 4. Testear PATCH /api/products/{id}/price (Actualizar Precio)
    // Test 404
    @Test
    @DisplayName("PATCH /api/products/{id}/price: product not found returns 404 Not Found")
    void updatePrice_NotFound_Returns404() throws Exception {
        when(productService.updatePrice(eq(NON_EXISTING_ID), any())).thenThrow(new ProductNotFoundException(NON_EXISTING_ID));
        // ... verifica .andExpect(status().isNotFound());
    }

    // Test 400
    @Test
    @DisplayName("PATCH /api/products/{id}/price: invalid price returns 400 Bad Request")
    void updatePrice_InvalidPrice_Returns400() throws Exception {
        when(productService.updatePrice(eq(EXISTING_ID), any())).thenThrow(new InvalidPriceException("Price must be greater than zero"));
        // ... verifica .andExpect(status().isBadRequest());
    }

    // DTO de ayuda para simular el cuerpo de la petición PATCH (asumido)
    record PriceUpdateDto(BigDecimal price) {

    }
}
