package com.biblioteca.catalogo.exception;

public class ConsultaLivroException extends Exception {

    public ConsultaLivroException(String message, Throwable ex) {
        super(message, ex);
    }

    public ConsultaLivroException(String message) {
        super(message);
    }

}
