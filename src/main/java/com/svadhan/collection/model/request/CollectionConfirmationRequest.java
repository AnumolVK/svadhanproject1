package com.svadhan.collection.model.request;

import com.svadhan.collection.constants.LoanPaidBy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionConfirmationRequest {
    private Long customerId;
    private Double paidAmount;
    private LoanPaidBy paidBy;
}
