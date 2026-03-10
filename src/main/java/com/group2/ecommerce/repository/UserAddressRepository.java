package com.group2.ecommerce.repository;

import com.group2.ecommerce.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    @Query("SELECT a FROM UserAddress a WHERE a.user.id = :userId AND a.isDefault = true")
    Optional<UserAddress> findDefaultAddressByUserId(@Param("userId") Long userId);

    List<UserAddress> findByUserId(Long userId);
}
