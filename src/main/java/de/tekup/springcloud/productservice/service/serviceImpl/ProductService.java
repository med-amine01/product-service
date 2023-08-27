package de.tekup.springcloud.productservice.service.serviceImpl;

import de.tekup.springcloud.productservice.dto.APIResponse;
import de.tekup.springcloud.productservice.dto.CouponResponse;
import de.tekup.springcloud.productservice.dto.ProductRequestDTO;
import de.tekup.springcloud.productservice.dto.ProductResponseDTO;
import de.tekup.springcloud.productservice.entity.Product;
import de.tekup.springcloud.productservice.exception.MicroserviceInvalidResponseException;
import de.tekup.springcloud.productservice.exception.ProductAlreadyExistsException;
import de.tekup.springcloud.productservice.exception.ProductNotFoundException;
import de.tekup.springcloud.productservice.exception.ProductServiceBusinessException;
import de.tekup.springcloud.productservice.repository.ProductRepository;
import de.tekup.springcloud.productservice.service.ProductServiceInterface;
import de.tekup.springcloud.productservice.util.ValueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductServiceInterface {
    
    private final ProductRepository productRepository;

    private final RestTemplate restTemplate;

    @Value("${coupon-service.url}")
    private String couponServiceURL;
    
    @Override
    public List<ProductResponseDTO> getProducts() throws ProductServiceBusinessException {
        try {
            log.info("ProductService::getProducts - Fetching Started.");
            
            List<Product> products = productRepository.findAll();
            
            List<ProductResponseDTO> productResponseDTOS = products.stream()
                    .map(ValueMapper::convertToProductResponseDto)
                    .toList();
            
            log.info("ProductService::getProducts - Fetched {} products", productResponseDTOS.size());
            
            log.info("ProductService::getProducts - Fetching Ends.");
            return productResponseDTOS;
            
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving products, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetching all products");
        }
    }
    
    @Override
    public ProductResponseDTO getProductById(Long id) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::getProductById - Fetching Started.");
            
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
            
            ProductResponseDTO productResponseDTO = ValueMapper.convertToProductResponseDto(product);
            
            log.debug("ProductService::getProductById - Product retrieved by ID: {} {}", id, ValueMapper.jsonToString(productResponseDTO));
            
            log.info("ProductService::getProductById - Fetching Ends.");
            return productResponseDTO;
            
        } catch (ProductNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving Product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetching product by id");
        }
    }
    
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        try {
            log.info("ProductService::createProduct - STARTED.");
            
            if (productRepository.existsByName(productRequestDTO.getName())) {
                throw new ProductAlreadyExistsException("Product with name " + productRequestDTO.getName() + " already exists");
            }

            Product product = ValueMapper.convertToEntity(productRequestDTO);
            
            // Retrieving coupon from coupon-service and map it to APIResponse
            ResponseEntity<APIResponse<CouponResponse>> responseEntity = restTemplate.exchange(
                    couponServiceURL + "/code/" + productRequestDTO.getCouponCode(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            
            APIResponse<CouponResponse> apiResponse = responseEntity.getBody();
            
            if (apiResponse.getStatus().equals("FAILED")) {
                if (!apiResponse.getErrors().isEmpty()) {
                    String errorDetails = apiResponse.getErrors().get(0).getErrorMessage();
                    
                    throw new MicroserviceInvalidResponseException(errorDetails);
                }
                throw new MicroserviceInvalidResponseException("Unknown error occurred.");
            }

            
            CouponResponse coupon = apiResponse.getResults();
            // Applying discount
            product.setPrice(productRequestDTO.getPrice().subtract(coupon.getDiscount()));
            
            // Saving product
            Product persistedProduct = productRepository.save(product);
            
            ProductResponseDTO productResponseDTO = ValueMapper.convertToProductResponseDto(persistedProduct);
            log.debug("ProductService::createProduct - product created : {}", ValueMapper.jsonToString(productResponseDTO));
            
            log.info("ProductService::createProduct - ENDS.");
            return productResponseDTO;
            
        } catch (MicroserviceInvalidResponseException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (ProductAlreadyExistsException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while persisting product, Exception message {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while creating a new product");
        }
    }
    
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::updateProduct - Started.");
            
            Product product = ValueMapper.convertToEntity(updatedProduct);
            
            // Fetch the existing product and update its properties
            ProductResponseDTO existingProduct = getProductById(id);
            product.setId(id);
            product.setCreatedAt(existingProduct.getCreatedAt());
            
            // Update the product and convert to response DTO
            Product persistedProduct = productRepository.save(product);
            ProductResponseDTO productResponseDTO = ValueMapper.convertToProductResponseDto(persistedProduct);

            
            log.debug("ProductService::updateProduct - Updated product: {}", ValueMapper.jsonToString(productResponseDTO));
            log.info("ProductService::updateProduct - Completed for ID: {}", id);
            
            return productResponseDTO;
            
        } catch (Exception exception) {
            log.error("Exception occurred while updating product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while updating product");
        }
    }

    
    @Override
    public void deleteProduct(Long id) {
        log.info("ProductService::deleteProduct - Starts.");
        
        try {
            log.info("ProductService::deleteProduct - Deleting product with ID: {}", id);
            
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
            productRepository.delete(product);
            
            log.info("ProductService::deleteProduct - Deleted product with ID: {}", id);
            
        } catch (ProductNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while deleting product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while deleting a product");
        }
        
        log.info("ProductService::deleteProduct - Ends.");
    }
}
