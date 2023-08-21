package com.svadhan.collection.repository;

import com.svadhan.collection.entity.Customer;
import com.svadhan.collection.entity.CustomerDoc;
import com.svadhan.collection.entity.DocTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDocRepository extends JpaRepository<CustomerDoc, Long> {

    Optional<CustomerDoc> findByCustomerAndDocType(Customer customer, DocTypeMaster docTypeMaster);
}
