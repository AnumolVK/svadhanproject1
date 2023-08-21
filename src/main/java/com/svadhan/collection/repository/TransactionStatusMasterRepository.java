package com.svadhan.collection.repository;

import com.svadhan.collection.entity.TransactionStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionStatusMasterRepository extends JpaRepository<TransactionStatusMaster, Long> {
    Optional<TransactionStatusMaster> findByStatus(String status);

    Optional<TransactionStatusMaster> findByPgw_IdAndStatus(Long pgwId, String status);

}
