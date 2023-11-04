package com.svadhan.collection.route;

import com.svadhan.collection.model.response.CustomerProfileResponse;
import com.svadhan.collection.service.CustomerDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerProfileCollection implements Function<Long, ResponseEntity<CustomerProfileResponse>> {

    private final CustomerDetailsService customerDetailsService;

    public CustomerProfileCollection(CustomerDetailsService customerDetailsService) {
        this.customerDetailsService = customerDetailsService;
    }


    @Override
    public ResponseEntity<CustomerProfileResponse> apply(Long customerId) {
        CustomerProfileResponse customerProfileDetails = customerDetailsService.getCustomerProfileDetails(customerId);
        return ResponseEntity.ok(customerProfileDetails);
    }
}
