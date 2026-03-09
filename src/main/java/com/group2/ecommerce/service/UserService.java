package com.group2.ecommerce.service;

import com.group2.ecommerce.dto.auth.RegisterRequest;
import com.group2.ecommerce.dto.user.UserRequest;
import com.group2.ecommerce.dto.user.UserResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;
import com.group2.ecommerce.entity.User;

public interface UserService {

    Page<UserResponse> getUsers(String query, int page);

    UserResponse findById(Long id);

    /** id == null → tạo mới, id != null → cập nhật. */
    void save(Long id, UserRequest request);

    Optional<User> authenticate(String email, String password);

    void toggleStatus(Long id);

    long countAll();

    void register(RegisterRequest request);
}

