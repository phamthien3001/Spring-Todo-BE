// src/main/java/com/example/todo/dto/request/LoginRequest.java
package com.example.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}