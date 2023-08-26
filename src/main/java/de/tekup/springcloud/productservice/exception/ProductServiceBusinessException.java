package de.tekup.springcloud.productservice.exception;

public class ProductServiceBusinessException extends RuntimeException {
    public ProductServiceBusinessException(String message) {
        super(message);
    }
}
