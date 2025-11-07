package co.edu.uniremington.products.service.impl;

import co.edu.uniremington.products.exception.InvalidPriceException;
import co.edu.uniremington.products.exception.ProductNotFoundException;
import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    // Se usa @Mock para crear un repositorio simulado (falso)
    @Mock
    private ProductRepository productRepository;

    // Se inyecta el mock del repositorio dentro del servicio real para hacer pruebas sin usar BD
    @InjectMocks
    private ProductServiceImpl productService;

    // Este método se ejecuta antes de cada prueba para inicializar los mocks
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // ✅ Test: Crear producto exitosamente
    // ---------------------------------------------------------
    @Test
    void shouldCreateProductSuccessfully() {
        // Creo un producto nuevo sin ID (como si fuera nuevo)
        Product product = new Product(null, "Laptop", "Gaming Laptop", new BigDecimal("1200"), 10);
        // Simulo que al guardarlo en BD, se genera el ID automáticamente
        Product savedProduct = new Product(1L, "Laptop", "Gaming Laptop", new BigDecimal("1200"), 10);

        // Le digo al mock que cuando se llame al save, devuelva el producto guardado
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Llamo al método real del servicio
        Product result = productService.createProduct(product);

        // Verifico que no sea nulo y que tenga los datos correctos
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        // Confirmo que el método save del repositorio se haya ejecutado una vez
        verify(productRepository, times(1)).save(product);
    }

    // ---------------------------------------------------------
    // ⚠️ Test: No crear producto si el nombre es nulo
    // ---------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenProductNameIsNull() {
        // Creo un producto sin nombre
        Product product = new Product(null, null, "Desc", new BigDecimal("100"), 5);

        // Espero que lance una excepción al intentar crearlo
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                productService.createProduct(product)
        );

        // Verifico el mensaje de error
        assertEquals("Product name is required", exception.getMessage());
        // Y confirmo que nunca se intentó guardar en la BD
        verify(productRepository, never()).save(any());
    }

    // ---------------------------------------------------------
    // ⚠️ Test: No crear producto con precio inválido
    // ---------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenPriceIsInvalid() {
        // Creo un producto con precio negativo
        Product product = new Product(null, "Mouse", "Wireless", new BigDecimal("-50"), 5);

        // Espero que se lance la excepción de precio inválido
        InvalidPriceException exception = assertThrows(InvalidPriceException.class, () ->
                productService.createProduct(product)
        );

        // Verifico el mensaje del error
        assertEquals("Price must be greater than zero", exception.getMessage());
        // Verifico que nunca se haya llamado al repositorio
        verify(productRepository, never()).save(any());
    }

    // ---------------------------------------------------------
    // 🔍 Test: Buscar producto por ID (exitoso)
    // ---------------------------------------------------------
    @Test
    void shouldFindProductByIdSuccessfully() {
        // Simulo un producto existente
        Product product = new Product(1L, "Keyboard", "Mechanical", new BigDecimal("80.00"), 15);

        // Digo que cuando se busque el ID 1, se devuelva ese producto
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Llamo al método de servicio
        Product result = productService.findById(1L);

        // Verifico que lo haya encontrado correctamente
        assertNotNull(result);
        assertEquals("Keyboard", result.getName());
        // Verifico que se haya hecho la búsqueda en el repositorio
        verify(productRepository).findById(1L);
    }

    // ---------------------------------------------------------
    // ❌ Test: Buscar producto por ID (no encontrado)
    // ---------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Simulo que no hay ningún producto con ese ID
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifico que se lance la excepción de no encontrado
        assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));
        // Verifico que sí se intentó buscar
        verify(productRepository).findById(1L);
    }

    // ---------------------------------------------------------
    // 📋 Test: Listar todos los productos
    // ---------------------------------------------------------
    @Test
    void shouldFindAllProducts() {
        // Simulo una lista con dos productos
        List<Product> products = List.of(
                new Product(1L, "Keyboard", "Mechanical", new BigDecimal("89.99"), 10),
                new Product(2L, "Mouse", "Wireless", new BigDecimal("49.99"), 15)
        );

        // Digo que cuando se llame a findAll, devuelva esa lista
        when(productRepository.findAll()).thenReturn(products);

        // Llamo al servicio para obtener los productos
        List<Product> result = productService.findAll();

        // Verifico que haya dos productos y que se haya llamado correctamente
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    // ---------------------------------------------------------
    // 💲 Test: Actualizar precio de un producto correctamente
    // ---------------------------------------------------------
    @Test
    void shouldUpdateProductPriceSuccessfully() {
        // Producto original
        Product product = new Product(1L, "Keyboard", "Mechanical", new BigDecimal("80.00"), 10);
        // Producto actualizado con nuevo precio
        Product updated = new Product(1L, "Keyboard", "Mechanical", new BigDecimal("100.00"), 10);

        // Simulo que el producto existe y que al guardarlo se actualiza
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        // Llamo al método para actualizar el precio
        Product result = productService.updatePrice(1L, new BigDecimal("100.00"));

        // Verifico que el precio se haya cambiado correctamente
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
    }

    // ---------------------------------------------------------
    // ⚠️ Test: Intentar actualizar con precio inválido
    // ---------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidPrice() {
        // Intento actualizar un producto con un precio negativo
        assertThrows(InvalidPriceException.class, () ->
                productService.updatePrice(1L, new BigDecimal("-10"))
        );

        // Verifico que nunca se haya buscado ni guardado nada
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }
}
