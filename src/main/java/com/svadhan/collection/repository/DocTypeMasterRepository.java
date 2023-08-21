package com.svadhan.collection.repository;

import com.svadhan.collection.constants.DocType;
import com.svadhan.collection.entity.DocTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocTypeMasterRepository extends JpaRepository<DocTypeMaster, Long> {
    Optional<DocTypeMaster> findByType(DocType docType);
}
