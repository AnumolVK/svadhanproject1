package com.svadhan.collection.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingCustomerCollectionResponse {
    private Long id;
    private String name;
    private String village;
    private String mobileNumber;
    private Double dueAmount;
    private String postOffice;
    private Integer dpd;
    private String pinCode;


}
