package com.group2.ecommerce.dto.user;

import com.group2.ecommerce.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận dữ liệu từ form (tạo / chỉnh sửa người dùng).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String phone;

    /** Chỉ bắt buộc khi tạo mới; để trống khi cập nhật = giữ nguyên mật khẩu cũ. */
    private String password;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;
}
