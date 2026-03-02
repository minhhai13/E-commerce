package com.group2.ecommerce.service;

import com.group2.ecommerce.dto.ProfileRequest;
import com.group2.ecommerce.dto.ProfileResponse;

public interface ProfileService {
    ProfileResponse getProfile(Long userId);

    ProfileResponse updateProfile(Long userId, ProfileRequest request);
}
