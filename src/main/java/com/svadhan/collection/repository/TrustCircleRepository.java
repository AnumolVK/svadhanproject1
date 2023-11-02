package com.svadhan.collection.repository;

import com.svadhan.collection.entity.TrustCircle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrustCircleRepository extends JpaRepository<TrustCircle, Long> {
    Optional<TrustCircle> findByCreatedFor(Long createdFor);

    @Query(value = "SELECT DISTINCT c.id  FROM CUSTOMER_OCR_DATA cod ,CUSTOMER c ,TRUST_CIRCLE tc ,TRUST_CIRCLE_CUSTOMER_MAPPING tccm WHERE c.ID =tccm.CUSTOMER_ID  AND tc.ID = tccm.TRUST_CIRCLE_ID AND cod.CUSTOMER_ID =c.ID  AND tc.CREATED_FOR  =:customerId AND tccm.STATUS = 'APPROVED'", nativeQuery = true)
    List<Long> getActiveTcMembersOfCustomer(@Param("customerId") Long customerId);
}
