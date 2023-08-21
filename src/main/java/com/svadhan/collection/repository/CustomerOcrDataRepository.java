package com.svadhan.collection.repository;

import com.svadhan.collection.entity.CustomerOcrData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerOcrDataRepository extends JpaRepository<CustomerOcrData, Long> {
    Optional<CustomerOcrData> findByCustomerId(Long customerId);

    List<CustomerOcrData> findAllByPinCode(String pinCode);

    List<CustomerOcrData> findAllByPinCodeIn(List <String> pinCodes);

}
