package com.svadhan.collection.model.response;

import lombok.Data;

@Data
public class CustomerTrustCircleResponse {

    private String name;
    private String village;
    private String phoneNumber;
    private Double paymentDue;
    private boolean isDueDatePassed;
    private String pinCode;
}
