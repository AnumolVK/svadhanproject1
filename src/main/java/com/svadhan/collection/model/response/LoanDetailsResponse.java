package com.svadhan.collection.model.response;

import lombok.Data;

import java.util.List;

@Data
public class LoanDetailsResponse {
    private Integer loansDue;
    private Double amountDue;
    private Integer collectedLoans;
    private Double collectedAmounts;
    private List<LoanDetailsDTO> loanDetailsDTOS;
    private List<TCDetailsDTO> tcDetailsDTOS;
}
