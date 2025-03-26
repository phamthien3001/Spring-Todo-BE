// src/main/java/com/example/todo/dto/request/RefreshTokenRequest.java
package com.example.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}