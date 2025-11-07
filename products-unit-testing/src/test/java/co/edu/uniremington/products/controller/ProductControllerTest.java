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

    // Se usa MockMvc para simular peticiones HTTP sin levantar el servidor
    @Autowired
    private MockMvc mockMvc;

    // Este mock reemplaza el servicio real para que no se conecte a la base de datos
    @MockBean
    private ProductService productService;

    // Sirve para convertir objetos Java a formato JSON y viceversa
    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    // ✅ Test: Crear producto exitosamente (POST /api/products)
    // ---------------------------------------------------------
    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        // Arrange: Se crea un producto de ejemplo
        Product product = new Product(1L, "Keyboard", "Mechanical Keyboard",
                new BigDecimal("89.99"), 15);

        // Se indica que cuando se llame al método createProduct, devuelva este producto
        Mockito.when(productService.createProduct(any(Product.class)))
                .thenReturn(product);

        // Act & Assert: Se simula una petición POST y se verifica la respuesta
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON) // tipo de contenido JSON
                        .content(objectMapper.writeValueAsString(product))) // cuerpo de la petición
                .andExpect(status().isCreated()) // se espera un código 201 (creado)
                .andExpect(jsonPath("$.name").value("Keyboard")) // verifica el nombre
                .andExpect(jsonPath("$.price").value(89.99)) // verifica el precio
                .andExpect(jsonPath("$.stock").value(15)); // verifica el stock
    }

    // ---------------------------------------------------------
    // 🔍 Test: Obtener producto por ID (GET /api/products/{id})
    // ---------------------------------------------------------
    @Test
    void shouldGetProductById() throws Exception {
        // Arrange: Se prepara un producto de prueba
        Product product = new Product(1L, "Mouse", "Wireless Mouse",
                new BigDecimal("49.99"), 20);

        // Cuando se llame a findById(1L), se devuelve este producto
        Mockito.when(productService.findById(1L)).thenReturn(product);

        // Act & Assert: Se hace la petición GET al endpoint con ID = 1
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk()) // debe responder con 200 OK
                .andExpect(jsonPath("$.name").value("Mouse")) // se verifica el nombre
                .andExpect(jsonPath("$.price").value(49.99)) // se verifica el precio
                .andExpect(jsonPath("$.stock").value(20)); // se verifica el stock
    }

    // ---------------------------------------------------------
    // 📋 Test: Listar todos los productos (GET /api/products)
    // ---------------------------------------------------------
    @Test
    void shouldListAllProducts() throws Exception {
        // Arrange: Se crea una lista simulada de productos
        List<Product> products = List.of(
                new Product(1L, "Keyboard", "Mechanical", new BigDecimal("89.99"), 15),
                new Product(2L, "Mouse", "Wireless", new BigDecimal("49.99"), 20)
        );

        // Se configura el mock para devolver esta lista cuando se llame al servicio
        Mockito.when(productService.findAll()).thenReturn(products);

        // Act & Assert: Se hace una petición GET para obtener todos los productos
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk()) // la respuesta debe ser 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // verifica que haya 2 productos
                .andExpect(jsonPath("$[0].name").value("Keyboard")) // primer producto
                .andExpect(jsonPath("$[1].name").value("Mouse")); // segundo producto
    }

    // ---------------------------------------------------------
    // 💲 Test: Actualizar precio (PATCH /api/products/{id}/price)
    // ---------------------------------------------------------
    @Test
    void shouldUpdateProductPrice() throws Exception {
        // Arrange: Se simula un producto actualizado
        Product updated = new Product(1L, "Keyboard", "Mechanical",
                new BigDecimal("99.99"), 15);

        // Cuando se llame al servicio para actualizar el precio, se devuelve el producto actualizado
        Mockito.when(productService.updatePrice(1L, new BigDecimal("99.99")))
                .thenReturn(updated);

        // Act & Assert: Se hace una petición PATCH pasando el nuevo precio por parámetro
        mockMvc.perform(patch("/api/products/1/price")
                        .param("price", "99.99")) // parámetro del nuevo precio
                .andExpect(status().isOk()) // respuesta 200 OK
                .andExpect(jsonPath("$.price").value(99.99)) // el precio debe ser el nuevo
                .andExpect(jsonPath("$.name").value("Keyboard")); // el nombre sigue igual
    }
}
