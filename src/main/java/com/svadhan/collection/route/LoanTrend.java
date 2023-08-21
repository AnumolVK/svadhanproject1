package com.svadhan.collection.route;

import com.svadhan.collection.model.request.LoanTrendRequest;
import com.svadhan.collection.model.response.LoanTrendDetailsDTO;
import com.svadhan.collection.service.LoansService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LoanTrend implements Function<LoanTrendRequest, ResponseEntity<LoanTrendDetailsDTO>> {

    private final LoansService loansService;

    public LoanTrend(LoansService loansService) {
        this.loansService = loansService;
    }

    @Override
    public ResponseEntity<LoanTrendDetailsDTO> apply(LoanTrendRequest loanTrendRequest) {
        LoanTrendDetailsDTO loanTrendDetailsDTO = loansService.getLoanTrend(loanTrendRequest);
        return ResponseEntity.ok(loanTrendDetailsDTO);
    }
}
