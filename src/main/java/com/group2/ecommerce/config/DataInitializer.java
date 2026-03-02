package com.group2.ecommerce.config;

import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.enums.Role;
import com.group2.ecommerce.repository.UserRepository;
import com.group2.ecommerce.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final String DEFAULT_ADMIN_EMAIL = "admin@gmail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "123";
    private static final String DEFAULT_ADMIN_FULL_NAME = "System Administrator";

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .email(DEFAULT_ADMIN_EMAIL)
                    .passwordHash(PasswordUtil.hashPassword(DEFAULT_ADMIN_PASSWORD))
                    .fullName(DEFAULT_ADMIN_FULL_NAME)
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
        }
    }
}
