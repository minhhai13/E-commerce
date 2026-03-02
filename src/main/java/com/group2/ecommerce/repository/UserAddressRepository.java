package com.group2.ecommerce.repository;

import com.group2.ecommerce.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);

    List<UserAddress> findByUserId(Long userId);
}
