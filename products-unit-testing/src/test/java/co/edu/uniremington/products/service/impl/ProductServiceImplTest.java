package co.edu.uniremington.products.service.impl;

import co.edu.uniremington.products.exception.InvalidPriceException;
import co.edu.uniremington.products.exception.ProductNotFoundException;
import co.edu.uniremington.products.model.Product;
import co.edu.uniremington.products.repository.ProductRepository;
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
import static org.mockito.Mockito.*;

/**
 * UNIT TESTING EXAM

 * Instructions:
 * 1. Implement unit tests for ProductServiceImpl
 * 2. Use Mockito to mock the ProductRepository
 * 3. Cover the following scenarios:
 *    - createProduct: successful case and validations
 *    - findById: successful case and when product not found
 *    - findAll: verify repository is called
 *    - updatePrice: successful case and validations

 * Evaluation criteria:
 * - Correct use of @Mock, @InjectMocks
 * - Use of when().thenReturn() and verify()
 * - Appropriate assertions
 * - Exception handling
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;


    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("2500.00"));

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setPrice(new BigDecimal("50.00"));
    }

    // TODO: Implement your tests here

    //Create Product
    @Test
    @DisplayName("AL crear un producto válido, ENTONCES devuelve el producto guardado.")
    void CreateProduct_whenCreatingValidProduct_thenReturnSavedProduct(){
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // When
        Product result = productService.createProduct(product1);

        // Then
        assertNotNull(result);
        assertEquals("Laptop", result.getName());

        //Verify
        verify(productRepository).save(product1);

    }

    @Test
    @DisplayName("Al crear un producto con un precio no válido, se debe lanzar una excepción InvalidPriceException.")
    void whenCreatingProductWithInvalidPrice_thenThrowException(){
        //Given
        product1.setPrice(BigDecimal.ZERO);

        assertThrows(InvalidPriceException.class, () -> productService.createProduct(product1));

        //Verify
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cuando encuentre el producto por su ID, devuélvalo.")
    void FindById_whenFindingById_thenReturnProduct(){
        //Given
        //When
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        //Then
        Product result = productService.findById(1L);

        assertEquals("Laptop", result.getName());

        //Verify
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Cuando no se encuentra el producto por su ID, se lanza una excepción ProductNotFoundException.")
    void Find_By_Id_whenFindingByIdNotFound_thenThrowException(){
        //Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(99L));

        //Verify
        verify(productRepository).findById(99L);

    }

    @Test
    @DisplayName("Cuando se encuentran todos los productos, se llama al repositorio una sola vez.")
    void FindAll_whenFindingAll_thenRepositoryIsCalled(){
        //Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());

        //Verify
        verify(productRepository).findAll();

    }

    @Test
    @DisplayName("Cuando actualice el precio con datos válidos, devuelva el producto actualizado.")
    void Update_whenUpdatingPrice_thenReturnUpdatedProduct(){
        //Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product result = productService.updatePrice(1L, new BigDecimal("280.000"));

        assertEquals(new BigDecimal("280.000"), result.getPrice());

        //Verify
        verify(productRepository).save(product1);

    }


    @Test
    @DisplayName("AL actualizar el precio con un valor no válido, se debe lanzar una excepción InvalidPriceException.")
    void Update_whenUpdatingPriceWithInvalidValue_thenThrowException(){
        //Given
        assertThrows(InvalidPriceException.class, () -> productService.updatePrice(1L, BigDecimal.ZERO));

        //Verify
        verify(productRepository, never()).save(any());

    }



}
