package com.intuit.inventory.management.exceptions;

public class ProductNotFoundException extends Exception {

    public ProductNotFoundException() { super(); }

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(Throwable cause) { super(cause); }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
