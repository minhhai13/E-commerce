package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.AddressRequest;
import com.group2.ecommerce.dto.AddressResponse;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.UserAddress;
import com.group2.ecommerce.repository.UserAddressRepository;
import com.group2.ecommerce.repository.UserRepository;
import com.group2.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        return userAddressRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasExisting = !userAddressRepository.findByUserId(userId).isEmpty();

        UserAddress address = UserAddress.builder()
                .user(user)
                .recipientName(request.getRecipientName())
                .phone(request.getPhone())
                .addressDetail(request.getAddressDetail())
                .isDefault(!hasExisting) // first address is automatically default
                .build();

        return toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setAddressDetail(request.getAddressDetail());

        return toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        boolean wasDefault = address.isDefault();
        userAddressRepository.delete(address);

        // If deleted address was default, set next available as default
        if (wasDefault) {
            userAddressRepository.findByUserId(userId).stream()
                    .findFirst()
                    .ifPresent(a -> {
                        a.setDefault(true);
                        userAddressRepository.save(a);
                    });
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // Remove default from all addresses of this user
        List<UserAddress> all = userAddressRepository.findByUserId(userId);
        all.forEach(a -> {
            a.setDefault(false);
            userAddressRepository.save(a);
        });

        // Set the target address as default
        UserAddress target = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!target.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        target.setDefault(true);
        userAddressRepository.save(target);
    }

    private AddressResponse toResponse(UserAddress a) {
        return AddressResponse.builder()
                .id(a.getId())
                .recipientName(a.getRecipientName())
                .phone(a.getPhone())
                .addressDetail(a.getAddressDetail())
                .isDefault(a.isDefault())
                .build();
    }
}
