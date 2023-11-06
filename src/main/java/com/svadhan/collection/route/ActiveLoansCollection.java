package com.svadhan.collection.route;

import com.svadhan.collection.model.response.LoanDetailsResponse;
import com.svadhan.collection.service.LoansService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ActiveLoansCollection implements Function<Long, ResponseEntity<LoanDetailsResponse>> {

    private final LoansService loansService;

    public ActiveLoansCollection(LoansService loansService) {
        this.loansService = loansService;
    }

    @Override
    public ResponseEntity<LoanDetailsResponse> apply(Long customerId) {
        LoanDetailsResponse activeLoans = loansService.getActiveLoans(customerId);
        return ResponseEntity.ok(activeLoans);
    }
}
