package com.svadhan.collection.model.response;

import lombok.Data;

@Data
public class DepositDetailsDTO {

    private String customerName;
    private String village;
    private String mobileNumber;
    private Double amount;
    private boolean isDuePending;
    private boolean isDueDatePassed;

}
