package com.svadhan.collection.repository;


import com.svadhan.collection.constants.LoanStatus;
import com.svadhan.collection.entity.LoanStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanStatusMasterRepository extends JpaRepository<LoanStatusMaster, Long> {
    Optional<LoanStatusMaster> findByStatus(LoanStatus loanStatus);
}