package com.svadhan.collection.service;


import com.svadhan.collection.model.response.CustomerTrustCircleResponse;

import java.util.List;

public interface TrustCircleService {

    List<CustomerTrustCircleResponse> getTrustCircleDetails(Long customerId);
}
