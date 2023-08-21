package com.svadhan.collection.service;

import com.svadhan.collection.entity.Transaction;
import com.svadhan.collection.model.request.CollectionConfirmationRequest;
import com.svadhan.collection.model.request.LoanTrendRequest;
import com.svadhan.collection.model.response.LoanDetailsDTO;
import com.svadhan.collection.model.response.LoanDetailsResponse;
import com.svadhan.collection.model.response.LoanTrendDetailsDTO;

public interface LoansService {

    LoanDetailsResponse getActiveLoans(Long customerId);

    LoanDetailsDTO getLoanPaymentHistory(Long loanId);

    LoanTrendDetailsDTO getLoanTrend(LoanTrendRequest loanTrendRequest);

    Transaction acknowledgeCollection(CollectionConfirmationRequest confirmationRequest);
}
