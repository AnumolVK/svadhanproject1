package com.svadhan.collection.model.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LoanDetailsDTO {

    private Long loanId;
    private LocalDateTime emiDueDate;
    private Double loanAmount;
    private Double emi;
    private String lenderName;
    private String lenderLogo;
    private Double paymentDue;
    private boolean isDueDatePassed;
    private Double loanOutstanding;
    private List<LoanPaymentHistoryDetailsDTO> loanPaymentHistoryDetailsDTOS;
    private LoanTrendDetailsDTO loanTrendDetailsDTO;
    private String maskedLenderLoanId;
}
