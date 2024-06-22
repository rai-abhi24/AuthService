package com.abhi.auth_service.controller;

import com.abhi.auth_service.payload.request.LoginRequest;
import com.abhi.auth_service.payload.request.RefreshTokenRequest;
import com.abhi.auth_service.payload.request.RegisterRequest;
import com.abhi.auth_service.payload.response.ApiResponse;
import com.abhi.auth_service.payload.response.JwtAuthResponse;
import com.abhi.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerNewUser(@RequestBody RegisterRequest registerRequest) {
        authService.registerNewUser(registerRequest);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        JwtAuthResponse jwtAuthResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthResponse> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtAuthResponse jwtAuthResponse = authService.refreshAccessToken(refreshTokenRequest);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    // TODO: Handle Request for forgotPassword
    // TODO: Handle Request for resetPassword
    // TODO: Handle Request for verifyEmail
    // TODO: Handle Request for logout
}
