// src/main/java/com/example/todo/service/AuthService.java
package com.example.todo.service;

import com.example.todo.dto.request.LoginRequest;
import com.example.todo.dto.request.RefreshTokenRequest;
import com.example.todo.dto.request.RegisterRequest;
import com.example.todo.dto.response.AuthResponse;
import com.example.todo.dto.response.JwtResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
    JwtResponse refreshToken(RefreshTokenRequest request);
    void logout();
}