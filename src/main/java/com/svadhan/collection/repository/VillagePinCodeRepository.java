package com.svadhan.collection.repository;

import com.svadhan.collection.entity.VillagePinCodeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillagePinCodeRepository extends JpaRepository<VillagePinCodeList, Long> {

    List<VillagePinCodeList> findByPinCode(String pinCode);
}
