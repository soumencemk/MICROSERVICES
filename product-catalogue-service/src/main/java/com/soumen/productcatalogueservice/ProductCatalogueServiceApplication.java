package com.soumen.productcatalogueservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.stream.Stream;

interface ProductRepository extends JpaRepository<Product, Long> {
}

@SpringBootApplication
@RefreshScope
public class ProductCatalogueServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogueServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner run(ProductRepository productRepository) {
        return args -> Stream.of(
                new Product(null, "Gaviscon", "Acid reflux medicine", 2.99),
                new Product(null, "Dulcoease", "Constipation", 4.00))
                .forEach(product -> productRepository.save(product));
    }

}

@RestController
@RequiredArgsConstructor
class ProductController {
    private final ProductRepository repository;
    Product EMPTY_PRODUCT = new Product(-1l, "ERROR", "NO SUCH PRODUCT", 0.00);

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return this.repository.findAll();
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        return this.repository.findById(id).orElse(EMPTY_PRODUCT);
    }

    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product) {
        return this.repository.save(product);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        this.repository.deleteById(id);
        return new ResponseEntity("SUCCESS !", HttpStatus.OK);
    }

}

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String desc;
    private double unitPrice;
}
