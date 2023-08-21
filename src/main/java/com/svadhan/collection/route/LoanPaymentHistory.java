package com.svadhan.collection.route;

import com.svadhan.collection.model.response.LoanDetailsDTO;
import com.svadhan.collection.service.LoansService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LoanPaymentHistory implements Function<Long, ResponseEntity<LoanDetailsDTO>> {

    private final LoansService loansService;

    public LoanPaymentHistory(LoansService loansService) {
        this.loansService = loansService;
    }

    @Override
    public ResponseEntity<LoanDetailsDTO> apply(Long loanId) {
        LoanDetailsDTO activeLoans = loansService.getLoanPaymentHistory(loanId);
        return ResponseEntity.ok(activeLoans);
    }
}
