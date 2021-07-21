package com.galvanize.invoicify.repository.adapter;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String s) {
        super("Error: " + s);
    }
}
