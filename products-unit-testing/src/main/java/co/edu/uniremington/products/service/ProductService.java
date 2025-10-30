package co.edu.uniremington.products.service;

import co.edu.uniremington.products.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    Product findById(Long id);

    List<Product> findAll();

    Product updatePrice(Long id, BigDecimal newPrice);
}
