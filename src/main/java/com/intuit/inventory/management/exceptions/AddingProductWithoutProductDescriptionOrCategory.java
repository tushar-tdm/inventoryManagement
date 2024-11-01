package com.intuit.inventory.management.exceptions;

public class AddingProductWithoutProductDescriptionOrCategory extends Exception {
    public AddingProductWithoutProductDescriptionOrCategory() { super(); }

    public AddingProductWithoutProductDescriptionOrCategory(String message) {
        super(message);
    }

    public AddingProductWithoutProductDescriptionOrCategory(Throwable cause) { super(cause); }

    public AddingProductWithoutProductDescriptionOrCategory(String message, Throwable cause) {
        super(message, cause);
    }

}
