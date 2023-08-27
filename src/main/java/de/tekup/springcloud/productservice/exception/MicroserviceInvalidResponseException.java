package de.tekup.springcloud.productservice.exception;

public class MicroserviceInvalidResponseException extends RuntimeException {
    public MicroserviceInvalidResponseException(String message) {
        super(message);
    }
}
