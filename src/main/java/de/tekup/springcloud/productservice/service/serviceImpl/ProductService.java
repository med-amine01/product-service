package de.tekup.springcloud.productservice.service.serviceImpl;

import de.tekup.springcloud.productservice.dto.ProductRequestDTO;
import de.tekup.springcloud.productservice.dto.ProductResponseDTO;
import de.tekup.springcloud.productservice.exception.ProductServiceBusinessException;
import de.tekup.springcloud.productservice.repository.ProductRepository;
import de.tekup.springcloud.productservice.service.ProductServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService implements ProductServiceInterface {

    private final ProductRepository productRepository;
    
    @Override
    public List<ProductResponseDTO> getProducts() throws ProductServiceBusinessException {
        return null;
    }
    
    @Override
    public ProductResponseDTO getProductById(Long id) throws ProductServiceBusinessException {
        return null;
    }
    
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        return null;
    }
    
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct) throws ProductServiceBusinessException {
        return null;
    }
    
    @Override
    public void deleteProduct(Long id) {
    
    }
}