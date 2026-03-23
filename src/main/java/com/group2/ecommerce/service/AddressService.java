package com.group2.ecommerce.service;

import com.group2.ecommerce.dto.AddressRequest;
import com.group2.ecommerce.dto.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddressesByUserId(Long userId);

    AddressResponse addAddress(Long userId, AddressRequest request);

    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    void deleteAddress(Long userId, Long addressId);

    void setDefaultAddress(Long userId, Long addressId);
}
