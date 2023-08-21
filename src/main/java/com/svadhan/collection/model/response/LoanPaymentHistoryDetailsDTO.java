package com.svadhan.collection.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanPaymentHistoryDetailsDTO {

    private String paymentMode;
    private LocalDateTime date;
    private String paidBy;
    private Double amount;
    private Boolean isDueAmount;


}
