package com.abhi.auth_service.service;

import com.abhi.auth_service.enums.TokenType;
import com.abhi.auth_service.model.Token;
import com.abhi.auth_service.model.User;
import com.abhi.auth_service.payload.request.LoginRequest;
import com.abhi.auth_service.payload.request.RefreshTokenRequest;
import com.abhi.auth_service.payload.request.RegisterRequest;
import com.abhi.auth_service.payload.response.JwtAuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    public void registerNewUser(RegisterRequest registerRequest) {
        System.out.println("Full name = " + registerRequest.getFirstName() + " " + registerRequest.getLastName());
        User newUser = userDetailsService.registerNewUser(registerRequest);

        // TODO: Produce message to kafka-topic
        // TODO: Generate Verification token & Send verification email
    }

    public JwtAuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateTokenFromEmail(authentication.getName());
        Token refreshToken = tokenService.generateAndSaveToken(authentication, TokenType.REFRESH_TOKEN);

        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public JwtAuthResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        Token refreshToken = tokenService.verifyTokenExpiration(
                refreshTokenRequest.getToken()
        );

        String email = refreshToken.getUser().getEmail();
        String accessToken = jwtService.generateTokenFromEmail(email);

        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password", ex);
        }
    }

    // TODO: Implement forgotPassword
    // TODO: Implement resetPassword
    // TODO: Implement verifyEmail
    // TODO: Implement logout
}
