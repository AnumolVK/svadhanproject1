package com.svadhan.collection.repository;

import com.svadhan.collection.entity.TrustCircle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrustCircleRepository extends JpaRepository<TrustCircle, Long> {
    Optional<TrustCircle> findByCreatedFor(Long createdFor);
}
