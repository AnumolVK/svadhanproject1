package com.svadhan.collection.route;

import com.svadhan.collection.entity.Transaction;
import com.svadhan.collection.model.request.CollectionConfirmationRequest;
import com.svadhan.collection.model.response.LoanDetailsResponse;
import com.svadhan.collection.service.LoansService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CollectionConfirmation implements Function<CollectionConfirmationRequest, ResponseEntity<?>> {

    private final LoansService loansService;

    public CollectionConfirmation(LoansService loansService) {
        this.loansService = loansService;
    }

    @Override
    public ResponseEntity<?> apply(CollectionConfirmationRequest confirmationRequest) {
        Transaction transaction = loansService.acknowledgeCollection(confirmationRequest);
        return ResponseEntity.ok("Success");
    }
}
