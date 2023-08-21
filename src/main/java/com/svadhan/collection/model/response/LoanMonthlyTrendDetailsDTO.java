package com.svadhan.collection.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanMonthlyTrendDetailsDTO {

    private String month;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private boolean isPaymentOnTime;
    private Integer dpd;
    private Integer dueDays;

}
