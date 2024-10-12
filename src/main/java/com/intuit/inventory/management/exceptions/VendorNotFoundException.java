package com.intuit.inventory.management.exceptions;

public class VendorNotFoundException extends Exception {
    public VendorNotFoundException() { super(); }

    public VendorNotFoundException(String message) {
        super(message);
    }

    public VendorNotFoundException(Throwable cause) { super(cause); }

    public VendorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
