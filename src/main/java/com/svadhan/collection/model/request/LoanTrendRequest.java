package com.svadhan.collection.model.request;

import lombok.Data;

@Data
public class LoanTrendRequest {
    private Long loanId;
    private String rangeType;
    private Integer range;

}
