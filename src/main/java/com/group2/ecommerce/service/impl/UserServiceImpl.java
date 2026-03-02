package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.user.UserRequest;
import com.group2.ecommerce.dto.user.UserResponse;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.enums.Role;
import com.group2.ecommerce.repository.UserRepository;
import com.group2.ecommerce.service.UserService;
import com.group2.ecommerce.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int PAGE_SIZE = 10;

    private final UserRepository userRepository;

    // ─────────────── Mapping ───────────────
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    // ─────────────── Queries ───────────────
    @Override
    public Page<UserResponse> getUsers(String query, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        Page<User> users = (query != null && !query.isBlank())
                ? userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
                : userRepository.findAll(pageable);
        return users.map(this::toResponse);
    }

    @Override
    public UserResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    // ─────────────── Commands ────────────────
    @Override
    @Transactional
    public void save(Long id, UserRequest request) {
        User user;

        if (id == null) {
            // ── Create ──────────────────────────────────
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("Mật khẩu không được để trống khi tạo mới.");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email '" + request.getEmail() + "' đã được sử dụng.");
            }
            user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .passwordHash(PasswordUtil.hashPassword(request.getPassword()))
                    .role(request.getRole())
                    .isActive(true)
                    .build();
        } else {
            // ── Update ──────────────────────────────────
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new IllegalArgumentException("Email '" + request.getEmail() + "' đã được sử dụng.");
            }
            user = findEntityById(id);
            if (user.isActive() && user.getRole() == Role.ADMIN && request.getRole() != Role.ADMIN) {
                long activeAdminCount = userRepository.countByRoleAndIsActiveTrue(Role.ADMIN);
                if (activeAdminCount <= 1) {
                    throw new IllegalArgumentException("Không thể thay đổi vai trò của Admin cuối cùng!");
                }
            }
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setRole(request.getRole());
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                user.setPasswordHash(PasswordUtil.hashPassword(request.getPassword()));
            }
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        User user = findEntityById(id);
        if (user.isActive() && user.getRole() == Role.ADMIN) {
            long activeAdminCount = userRepository.countByRoleAndIsActiveTrue(Role.ADMIN);
            if (activeAdminCount <= 1) {
                throw new IllegalArgumentException("Không thể vô hiệu hóa Admin cuối cùng trong hệ thống!");
            }
        }
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    public long countAll() {
        return userRepository.count();
    }

    // ─────────────── Private helpers ────────────────
    private User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + id));
    }
}
