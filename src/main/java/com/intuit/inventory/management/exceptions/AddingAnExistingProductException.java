package com.intuit.inventory.management.exceptions;

public class AddingAnExistingProductException extends Exception{

    public AddingAnExistingProductException() { super(); }

    public AddingAnExistingProductException(String message) {
        super(message);
    }

    public AddingAnExistingProductException(Throwable cause) { super(cause); }

    public AddingAnExistingProductException(String message, Throwable cause) {
        super(message, cause);
    }


}
