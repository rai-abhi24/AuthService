package com.abhi.auth_service.service;

import com.abhi.auth_service.enums.RoleName;
import com.abhi.auth_service.exception.customException.EmailAlreadyExistsException;
import com.abhi.auth_service.exception.customException.ResourceNotFoundException;
import com.abhi.auth_service.model.Role;
import com.abhi.auth_service.model.User;
import com.abhi.auth_service.payload.request.RegisterRequest;
import com.abhi.auth_service.repository.RoleRepository;
import com.abhi.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findUserByEmail(email);
        return new CustomUserDetails(user);
    }

    @Transactional
    public User registerNewUser(RegisterRequest registerRequest) {
        validateUserExistence(registerRequest.getEmail());

        User newUser = buildNewUser(registerRequest);
        assignDefaultRoleToUser(newUser);

        return userRepository.save(newUser);
    }

    public User buildNewUser(RegisterRequest registerRequest) {
        return User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .emailVerified(false)
                .build();
    }

    private void assignDefaultRoleToUser(User user) {
        Role defaultRole = findDefaultRole();
        user.setRoles(Collections.singleton(defaultRole));
    }

    private Role findDefaultRole() {
        return roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private void validateUserExistence(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("User with this email is already registered: " + email);
        }
    }
}
