package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.ProfileRequest;
import com.group2.ecommerce.dto.ProfileResponse;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.UserAddress;
import com.group2.ecommerce.repository.UserAddressRepository;
import com.group2.ecommerce.repository.UserRepository;
import com.group2.ecommerce.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    @Override
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserAddress address = userAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseGet(() -> userAddressRepository.findByUserId(userId)
                        .stream().findFirst().orElse(null));

        return buildResponse(user, address);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getUserPhone());
        userRepository.save(user);

        UserAddress address = userAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseGet(() -> userAddressRepository.findByUserId(userId)
                        .stream().findFirst().orElse(null));

        if (address == null) {
            address = new UserAddress();
            address.setUser(user);
            address.setDefault(true);
        }

        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getAddressPhone());
        address.setAddressDetail(request.getAddressDetail());
        userAddressRepository.save(address);

        return buildResponse(user, address);
    }

    private ProfileResponse buildResponse(User user, UserAddress address) {
        ProfileResponse.ProfileResponseBuilder builder = ProfileResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .userPhone(user.getPhone());

        if (address != null) {
            builder.addressId(address.getId())
                    .recipientName(address.getRecipientName())
                    .addressPhone(address.getPhone())
                    .addressDetail(address.getAddressDetail());
        }

        return builder.build();
    }
}
