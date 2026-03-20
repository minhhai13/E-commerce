package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.LoginRequest;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.repository.UserRepository;
import com.group2.ecommerce.service.AuthService;
import com.group2.ecommerce.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Your account is disabled");
        }

        return user;
    }
}
