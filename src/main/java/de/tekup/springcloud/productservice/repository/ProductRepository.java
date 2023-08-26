package de.tekup.springcloud.productservice.repository;

import de.tekup.springcloud.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
