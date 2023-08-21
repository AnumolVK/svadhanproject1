package com.svadhan.collection.model.api8;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRepaymentRequest {
    @JsonProperty("loanId")
    private String loanId;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("repaymentMadeThrough")
    private String repaymentMadeThrough;
    @JsonProperty("transactionDate")
    private String transactionDate;
    @JsonProperty("transactionReferenceNo")
    private Long transactionReferenceNo;
}