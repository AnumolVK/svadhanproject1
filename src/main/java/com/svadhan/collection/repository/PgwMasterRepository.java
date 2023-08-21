package com.svadhan.collection.repository;

import com.svadhan.collection.constants.PgwName;
import com.svadhan.collection.entity.PGWMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PgwMasterRepository extends JpaRepository<PGWMaster, Long> {
    Optional<PGWMaster> findByName(PgwName pgwName);
}
