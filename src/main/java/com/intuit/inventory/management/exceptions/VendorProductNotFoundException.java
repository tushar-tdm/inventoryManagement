package com.intuit.inventory.management.exceptions;

public class VendorProductNotFoundException extends Exception {
    public VendorProductNotFoundException() { super(); }

    public VendorProductNotFoundException(String message) {
        super(message);
    }

    public VendorProductNotFoundException(Throwable cause) { super(cause); }

    public VendorProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
