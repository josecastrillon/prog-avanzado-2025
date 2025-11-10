package co.edu.uniremington.products.service;

import co.edu.uniremington.products.exception.InvalidPriceException;
import co.edu.uniremington.products.exception.ProductNotFoundException;
import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
import co.edu.uniremington.products.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * UNIT TESTING EXAM
 *
 * Instructions:
 * 1. Implement unit tests for ProductServiceImpl
 * 2. Use Mockito to mock the ProductRepository
 * 3. Cover the following scenarios:
 *    - createProduct: successful case and validations
 *    - findById: successful case and when product not found
 *    - findAll: verify repository is called
 *    - updatePrice: successful case and validations
 *
 * Evaluation criteria:
 * - Correct use of @Mock, @InjectMocks
 * - Use of when().thenReturn() and verify()
 * - Appropriate assertions
 * - Exception handling
 */


@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    // ... [Variables, @Mock, @InjectMocks y @BeforeEach ] ...
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    //declaraciones
    private Product laptop;
    private Product monitor;
    private final Long EXISTING_ID = 1L;
    private final Long NON_EXISTING_ID = 99L;
    @BeforeEach


    void setUp() {
        // Objeto base para tests, usando el constructor de 5 parámetros:
        // Product(Long id, String name, String description, BigDecimal price, Integer stock)
        laptop = new Product(EXISTING_ID, "Laptop Gamer", null, new BigDecimal("1500.00"), 10);
        monitor = new Product(2L, "Monitor 4K", null, new BigDecimal("500.00"), 5);

    }

        // 1. createProduct (Verificar que el producto válido se guarda y se devuelve.)
    @Test
    @DisplayName("GIVEN a valid product WHEN creating THEN the product is saved and returned")
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(laptop);

        // Usar el constructor de 5 parámetros, con nulls o valores por defecto para los no requeridos
        Product productToCreate = new Product(null, "Laptop Gamer", "Descripción", new BigDecimal("1500.00"), 10);

        Product savedProduct = productService.createProduct(productToCreate);

        assertNotNull(savedProduct);
        verify(productRepository, times(1)).save(productToCreate);
    }

    // 2. TESTS: findById()  Caso Valido y caso de Falla
    @Test
    @DisplayName("GIVEN an existing ID WHEN finding product THEN return the Product (Successful Case)")
    void findById_Success() {
        // Configuración (Arrange):
        // 1. Decirle al mockRepository qué hacer cuando se le llama con el ID existente.
        when(productRepository.findById(EXISTING_ID)).thenReturn(Optional.of(laptop));

        // Ejecución (Act):
        Product foundProduct = productService.findById(EXISTING_ID);

        // Verificación (Assert):
        // 1. Asegurar que el objeto no es nulo y tiene el ID correcto.
        assertNotNull(foundProduct, "El producto encontrado no debe ser nulo.");
        assertEquals(EXISTING_ID, foundProduct.getId(), "El ID del producto debe coincidir con el buscado.");

        // 2. Verificar que SÍ se llamó al método findById del repositorio exactamente una vez.
        verify(productRepository, times(1)).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GIVEN a non-existing ID WHEN finding product THEN throw ProductNotFoundException (Not Found Case)")
    void findById_NotFound_ThrowsException() {
        // Configuración (Arrange):
        // 1. Decirle al mockRepository que, para un ID no existente, devuelva un Optional vacío.
        when(productRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        // Ejecución y Verificación (Act & Assert):
        // 1. Usar assertThrows para verificar que la excepción correcta es lanzada.
        assertThrows(ProductNotFoundException.class, () -> {
            productService.findById(NON_EXISTING_ID);
        }, "Debe lanzar ProductNotFoundException si el producto no se encuentra.");

        // 2. Verificar que SÍ se llamó al método findById, pero NO se llamó a save u otros métodos.
        verify(productRepository, times(1)).findById(NON_EXISTING_ID);
    }


    // 3. TESTS: findAll() Caso Valido y caso de Falla

    @Test
    @DisplayName("WHEN finding all products THEN verify call to repository and return list")
    void findAll_Success() {
        // Given (Configuración):
        // Usamos las variables de clase, asegurándonos de que monitor esté inicializado.
        Product monitor = new Product(2L, "Monitor 4K", "Desc", new BigDecimal("500.00"), 5);
        List<Product> products = Arrays.asList(laptop, monitor);

        // Decirle al mockRepository que devuelva la lista de productos
        when(productRepository.findAll()).thenReturn(products);

        // When (Ejecución):
        List<Product> result = productService.findAll();

        // Then (Verificación):
        assertNotNull(result, "La lista no debe ser nula.");
        assertEquals(2, result.size(), "Debe devolver los dos productos simulados.");

        // Verificación clave: Asegura que el servicio llamó *exactamente una vez* al método findAll del repositorio.
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("WHEN finding all products but none exist THEN return an empty list")
    void findAll_EmptyList() {
        // Given (Configuración):
        List<Product> emptyList = List.of();

        // Decirle al mockRepository que devuelva una lista vacía
        when(productRepository.findAll()).thenReturn(emptyList);

        // When (Ejecución):
        List<Product> result = productService.findAll();

        // Then (Verificación):
        assertNotNull(result, "El resultado debe ser una lista vacía (no nula).");
        assertTrue(result.isEmpty(), "La lista devuelta debe estar vacía.");

        // Verificación clave: Asegura que el servicio llamó al repositorio.
        verify(productRepository, times(1)).findAll();
    }

    // 4. TESTS: updatePrice() Caso Valido y caso de Falla
    @Test
    @DisplayName("GIVEN a valid price and existing ID WHEN updating price THEN the product is saved successfully")
    void updatePrice_Success() {
        BigDecimal oldPrice = laptop.getPrice();
        BigDecimal newPrice = new BigDecimal("1600.00");

        // 1. Simular la búsqueda inicial (findById)
        // El servicio llama findById(EXISTING_ID)
        when(productRepository.findById(EXISTING_ID)).thenReturn(Optional.of(laptop));

        // 2. Simular el guardado
        // Simula que save devuelve el objeto 'laptop' después de que su precio es modificado en el servicio
        when(productRepository.save(any(Product.class))).thenReturn(laptop);

        // When (Ejecución)
        Product updatedProduct = productService.updatePrice(EXISTING_ID, newPrice);

        // Then (Verificación)
        assertNotNull(updatedProduct, "El producto actualizado no debe ser nulo.");
        assertEquals(newPrice, updatedProduct.getPrice(), "El precio debe ser el nuevo precio.");

        // Verificación clave: Asegura que se llamó a findById y luego a save
        verify(productRepository, times(1)).findById(EXISTING_ID);
        verify(productRepository, times(1)).save(laptop);

        // Opcional: Asegurar que el precio fue realmente cambiado
        assertNotEquals(oldPrice, updatedProduct.getPrice());
    }

    @Test
    @DisplayName("GIVEN non-existing ID WHEN updating price THEN throw ProductNotFoundException")
    void updatePrice_NotFound_ThrowsException() {
        BigDecimal newPrice = new BigDecimal("1600.00");

        // 1. Simular que el producto no se encuentra
        // La validación de precio pasa, luego se llama a findById.
        when(productRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        // When & Then (Verificación de la excepción)
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updatePrice(NON_EXISTING_ID, newPrice);
        }, "Debe lanzar ProductNotFoundException si el producto no se encuentra.");

        // Verificación clave: Asegura que solo se llamó a findById y que save NUNCA se llamó
        verify(productRepository, times(1)).findById(NON_EXISTING_ID);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("GIVEN a negative price WHEN updating price THEN throw InvalidPriceException")
    void updatePrice_NegativePrice_ThrowsException() {
        BigDecimal invalidPrice = new BigDecimal("-5.00");

        // When & Then (Verificación de la excepción de validación de negocio)
        // El servicio debe llamar a validatePrice(newPrice) antes de llamar a findById(id).
        assertThrows(InvalidPriceException.class, () -> {
            productService.updatePrice(EXISTING_ID, invalidPrice);
        }, "Debe lanzar InvalidPriceException si el precio es negativo o cero.");

        // Verificación clave: Asegura que NO se intenta buscar (findById) ni guardar (save)
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }
}
