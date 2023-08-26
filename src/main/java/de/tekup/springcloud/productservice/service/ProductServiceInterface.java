package de.tekup.springcloud.productservice.service;

import de.tekup.springcloud.productservice.dto.ProductRequestDTO;
import de.tekup.springcloud.productservice.dto.ProductResponseDTO;
import de.tekup.springcloud.productservice.exception.ProductServiceBusinessException;

import java.util.List;

public interface ProductServiceInterface {
    
    List<ProductResponseDTO> getProducts() throws ProductServiceBusinessException;
    
    ProductResponseDTO getProductById(Long id) throws ProductServiceBusinessException;
    
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct) throws ProductServiceBusinessException;
    
    void deleteProduct(Long id);
}
