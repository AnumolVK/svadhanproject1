package com.svadhan.collection.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponse {
    private Long id;
    private String fullName;
    private String photo;
    private String housePhoto;
    private HouseLocationDTO houseLocation;
    private String mobile;
    private String address;
    private String voterIdNumber;
    private String aadharNumber;
    private String village;
    private String pinCode;
    private String postOffice;
    private ContinuingGuarantorDTO continuingGuarantorDTO;
}
