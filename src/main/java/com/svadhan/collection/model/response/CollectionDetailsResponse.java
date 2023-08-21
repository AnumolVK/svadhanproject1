package com.svadhan.collection.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDetailsResponse {
    private Double totalAmountCollected;
    private Long timeLeftToDeposit;
    private double depositedSum;
    private double depositPendingSum;
    private List<DepositDetailsDTO> depositedDetailsDTOS;
    private List<DepositDetailsDTO> depositPendingDetailsDTOS;

}
