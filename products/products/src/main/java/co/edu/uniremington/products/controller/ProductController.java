package co.edu.uniremington.products.controller;


import co.edu.uniremington.products.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private List<Product> productlist = new ArrayList<>(List.of(
            new Product(1L, "computador", 50000000D),
            new Product(2L, "monitor", 2500000D)
    ));
    @GetMapping
    public List<Product> getproductlist() {
        System.out.println("Consulta de productos muy lentaaaaaa");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("FINALIZADA LA PETICION");
        return productlist;
    }

}
