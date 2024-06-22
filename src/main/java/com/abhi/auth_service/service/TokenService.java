package com.abhi.auth_service.service;

import com.abhi.auth_service.enums.TokenType;
import com.abhi.auth_service.exception.customException.TokenExpiredException;
import com.abhi.auth_service.exception.customException.TokenNotFoundException;
import com.abhi.auth_service.model.Token;
import com.abhi.auth_service.model.User;
import com.abhi.auth_service.repository.TokenRepository;
import com.abhi.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${token.refresh.expiry}")
    private Long tokenExpiryDuration;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public Token generateAndSaveToken(Authentication authentication, TokenType tokenType) {
        User user = getUserByEmail(authentication.getName());

        Token existingToken = findTokenByUserAndType(user, tokenType);

        String tokenValue = generateTokenValue();

        if (existingToken != null) {
            updateToken(existingToken, tokenValue);
            return tokenRepository.save(existingToken);
        }

        Token newToken = createNewToken(tokenValue, tokenType, user);
        return tokenRepository.save(newToken);
    }

    private Token createNewToken(String tokenValue, TokenType tokenType, User user) {
        return Token.builder()
                .token(tokenValue)
                .expiry(Instant.now().plusMillis(tokenExpiryDuration))
                .type(tokenType)
                .user(user)
                .build();
    }

    public Token verifyTokenExpiration(String tokenValue) {
        Token token = findTokenByTokenValue(tokenValue);

        if (token.getExpiry().isBefore(Instant.now())) {
            tokenRepository.delete(token);
            throw new TokenExpiredException("Token has expired. Please sign in again");
        }

        return token;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private Token findTokenByTokenValue(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenNotFoundException("Invalid token"));
    }

    private Token findTokenByUserAndType(User user, TokenType tokenType) {
        return tokenRepository.findByUserIdAndType(user.getId(), tokenType)
                .orElse(null);
    }

    private String generateTokenValue() {
        return UUID.randomUUID().toString();
    }

    private void updateToken(Token token, String newTokenValue) {
        token.setToken(newTokenValue);
        token.setExpiry(Instant.now().plusMillis(tokenExpiryDuration));
    }
}
