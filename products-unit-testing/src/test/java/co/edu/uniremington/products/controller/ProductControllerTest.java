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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

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
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("250.000"));

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setPrice(new BigDecimal("50.000"));
    }

    // TODO: Implement your tests here

    //api/product
    //POST
    @Test
    @DisplayName("Al publicar un producto válido, se debe devolver el código 201: Creado con datos del producto.")
    void api_product_whenPostingValidProduct_thenReturn201() throws Exception {
        // Given
        when(productService.createProduct(any(Product.class))).thenReturn(product1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(250.000));

        verify(productService).createProduct(any(Product.class));
    }

    //api/products/{id}
    //GET BY ID
    @Test
    @DisplayName("Cuando se obtiene el producto por su ID, se devuelve un código 200 OK con los datos del producto.")
    void api_products_id_whenGettingProductById_thenReturn200() throws Exception {
        when(productService.findById(1L)).thenReturn(product1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(250.000)));

        verify(productService).findById(1L);
    }

    //api/products
    //GET
    @Test
    @DisplayName("Cuando reciba todos los productos, devuelva la lista con 200 OK.")
    void api_products_whenGettingAllProducts_thenReturn200() throws Exception{
        //Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.findAll()).thenReturn(products);

        //
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(250.000)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Mouse")))
                .andExpect(jsonPath("$[1].price", is(50.000)));

        //Verify
        verify(productService).findAll();

    }

    //api/products/{id}/price
    //PATCH
    @Test
    @DisplayName("Al actualizar el precio mediante PATCH, devuelve 200 OK con el producto actualizado.")
    void api_products_id_price_whenUpdatingPrice_thenReturn200() throws Exception{
        //Given
        product1.setPrice(new BigDecimal("300.000"));
        when(productService.updatePrice(1L, new BigDecimal("300.000"))).thenReturn(product1);

        //
        mockMvc.perform(patch("/api/products/1/price")
                .param("price", "300.000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(300.000)));

        //Verify
        verify(productService).updatePrice(1L, new BigDecimal("300.000"));

    }



}
