package com.svadhan.collection.route;

import com.svadhan.collection.model.response.CustomerTrustCircleResponse;
import com.svadhan.collection.service.TrustCircleService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;

public class TrustCircleDetails implements Function<Long, ResponseEntity<List<CustomerTrustCircleResponse>>> {
    private final TrustCircleService circleService;

    public TrustCircleDetails(TrustCircleService circleService) {
        this.circleService = circleService;
    }

    @Override
    public ResponseEntity<List<CustomerTrustCircleResponse>> apply(Long customerId) {
        List<CustomerTrustCircleResponse> trustCircleDetailsForCustomer = circleService.getTrustCircleDetails(customerId);
        return ResponseEntity.ok(trustCircleDetailsForCustomer);
    }
}
