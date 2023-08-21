package com.svadhan.collection.repository;

import com.svadhan.collection.entity.TrustCircleCustomerMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrustCircleCustomerMappingRepository extends JpaRepository<TrustCircleCustomerMapping, Long> {

    List<TrustCircleCustomerMapping> findAllByTrustCircleId(Long trustCircleId);
}
