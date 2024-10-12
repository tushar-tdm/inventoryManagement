package com.intuit.inventory.management.exceptions;

public class AddingProductWithoutProductNameOrCategory extends Exception {
    public AddingProductWithoutProductNameOrCategory() { super(); }

    public AddingProductWithoutProductNameOrCategory(String message) {
        super(message);
    }

    public AddingProductWithoutProductNameOrCategory(Throwable cause) { super(cause); }

    public AddingProductWithoutProductNameOrCategory(String message, Throwable cause) {
        super(message, cause);
    }

}
