package com.biblioteca.catalogo.exception;

public class ApiExecutionException extends Exception {

    public ApiExecutionException(String message, Throwable ex) {
        super(message, ex);
    }

    public ApiExecutionException(String message) {
        super(message);
    }
}
