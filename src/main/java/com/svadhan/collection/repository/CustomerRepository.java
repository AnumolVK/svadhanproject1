package com.svadhan.collection.repository;

import com.svadhan.collection.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByMobile(String mobNumber);

    List<Customer> findByDeviceId(String deviceId);

    List<Customer> findTop10ByIdInOrderByCreateDateTimeDesc(List<Long> ids);

    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT * FROM CUSTOMER WHERE ID IN (:ids) AND LOWER(NAME) LIKE %:name% ORDER BY CREATED_ON DESC) WHERE ROWNUM <= 10")
    List<Customer> findTop10ByIdInAndNameLikeOrderByCreateDateTimeDesc(@Param("ids") List<Long> ids, @Param("name") String name);

    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT * FROM CUSTOMER WHERE ID IN (:ids) AND MOBILE LIKE %:mobile% ORDER BY CREATED_ON DESC) WHERE ROWNUM <= 10")
    List<Customer> findTop10ByIdInAndNumberLikeOrderByCreateDateTimeDesc(@Param("ids") List<Long> ids, @Param("mobile") String mobile);

    List<Customer> findAllByAssociatedAgentIdAndHasRegisterProcessCompleted(Long associatedAgentId, Boolean hasRegisterProcessCompleted);
}
