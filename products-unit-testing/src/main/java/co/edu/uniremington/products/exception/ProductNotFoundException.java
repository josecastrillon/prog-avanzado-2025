package co.edu.uniremington.products.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found with ID: " + id);
    }
}
