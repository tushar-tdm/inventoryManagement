package com.intuit.inventory.management.exceptions;

public class AddingNewVendorWithoutVendorLinkException extends Exception {
    public AddingNewVendorWithoutVendorLinkException() { super(); }

    public AddingNewVendorWithoutVendorLinkException(String message) {
        super(message);
    }

    public AddingNewVendorWithoutVendorLinkException(Throwable cause) { super(cause); }

    public AddingNewVendorWithoutVendorLinkException(String message, Throwable cause) {
        super(message, cause);
    }

}
