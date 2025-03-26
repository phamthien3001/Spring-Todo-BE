package com.example.todo.service.impl;

import com.example.todo.dto.request.LoginRequest;
import com.example.todo.dto.request.RefreshTokenRequest;
import com.example.todo.dto.request.RegisterRequest;
import com.example.todo.dto.response.AuthResponse;
import com.example.todo.dto.response.JwtResponse;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.model.ERole;
import com.example.todo.model.RefreshToken;
import com.example.todo.model.Role;
import com.example.todo.model.User;
import com.example.todo.repository.RoleRepository;
import com.example.todo.repository.UserRepository;
import com.example.todo.security.JwtUtils;
import com.example.todo.security.RefreshTokenService;
import com.example.todo.security.UserDetailsImpl;
import com.example.todo.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils,
                           RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return new AuthResponse("User registered successfully!", user.getUsername(), user.getEmail());
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getId()).getToken();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(
                accessToken,
                refreshToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );
    }

    @Override
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return new JwtResponse(
                            token,
                            requestRefreshToken,
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            null // hoặc có thể truyền roles nếu cần
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @Override
    public void logout() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        refreshTokenService.deleteByUserId(userDetails.getId());
    }
}