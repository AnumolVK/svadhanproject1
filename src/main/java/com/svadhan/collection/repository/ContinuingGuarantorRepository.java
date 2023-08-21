package com.svadhan.collection.repository;


import com.svadhan.collection.entity.ContinuingGuarantor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContinuingGuarantorRepository extends JpaRepository<ContinuingGuarantor, Long> {

    Optional<ContinuingGuarantor> findById(Long id);
}
