package com.itterior.itterior.exception;

public class CustomUserNotFoundException extends RuntimeException{
    public CustomUserNotFoundException(String message) {
        super(message);
    }
}
