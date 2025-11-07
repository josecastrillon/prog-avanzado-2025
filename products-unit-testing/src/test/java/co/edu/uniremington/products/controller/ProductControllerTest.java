package co.edu.uniremington.products.controller;

import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UNIT TESTING EXAM
 *
 * ProductControllerTest
 *
 * Debe probar:
 * ✅ POST /api/products (creación exitosa con status 201)
 * ✅ GET /api/products/{id} (obtener producto existente)
 * ✅ GET /api/products (listar todos los productos)
 * ✅ PATCH /api/products/{id}/price (actualizar precio)
 *
 * Requisitos:
 * - Uso de @WebMvcTest
 * - Uso de @MockBean para el servicio
 * - Verificación de status HTTP y contenido JSON
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
        // Instancias base para las pruebas, rellenar según sea necesario
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setDescription("High-end gaming laptop");
        product1.setPrice(new BigDecimal("2500.00"));
        product1.setStock(5);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Smartphone");
        product2.setDescription("Android phone");
        product2.setPrice(new BigDecimal("2500.0"));
        product2.setStock(20);
    }

    /**
     * POST /api/products
     * Caso exitoso: creación de producto
     * Debe retornar status 201 y el producto en el body
     */
    @Test
    void shouldCreateProductAndReturnStatus201() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product1); //simula la creacióel producto

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1))) //mapea el producto en json y lo envia en el body
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(2500.00))
                .andExpect(jsonPath("$.stock").value(5));
    }

    /**
     * GET /api/products/{id}
     * Caso exitoso: producto existente
     */
    @Test
    void shouldReturnExistingProductById() throws Exception {
        when(productService.findById(1L)).thenReturn(product1);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("High-end gaming laptop"))
                .andExpect(jsonPath("$.price").value(2500.00))
                .andExpect(jsonPath("$.stock").value(5));
    }

    /**
     * GET /api/products
     * Caso exitoso: lista de productos
     */
    @Test
    void shouldListAllProducts() throws Exception {
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Smartphone"));
    }

    /**
     * PATCH /api/products/{id}/price
     * Caso exitoso: actualización de precio
     */
    @Test
    void shouldUpdateProductPrice() throws Exception {
        Product updatedProduct = new Product(1L, "Laptop", "High-end gaming laptop", new BigDecimal("3000.00"), 5);
        when(productService.updatePrice(eq(1L), eq(new BigDecimal("3000.00")))).thenReturn(updatedProduct);

        mockMvc.perform(patch("/api/products/1/price")
                        .param("price", "3000.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(3000.00));
    }
}
