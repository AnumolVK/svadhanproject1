package com.svadhan.collection.model.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LoanTrendDetailsDTO {

    private Integer calls;
    private Integer visits;
    private Integer dpd;
    private List<Map<String, Object>> loanMonthlyTrendDetailsDTOList;
    private Double paidBySelf;
    private Double paidByTCMember;
    private Double paidByCash;
    private Double paidByDigital;
}
