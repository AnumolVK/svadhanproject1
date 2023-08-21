package com.svadhan.collection.service;


import com.svadhan.collection.entity.Customer;
import com.svadhan.collection.model.response.CustomerProfileResponse;

public interface CustomerDetailsService {
    Customer getCustomerById(Long customerId);

    CustomerProfileResponse getCustomerProfileDetails(Long customerId);

}
