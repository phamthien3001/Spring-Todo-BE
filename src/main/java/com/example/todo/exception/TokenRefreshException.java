// src/main/java/com/example/todo/exception/TokenRefreshException.java
package com.example.todo.exception;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}