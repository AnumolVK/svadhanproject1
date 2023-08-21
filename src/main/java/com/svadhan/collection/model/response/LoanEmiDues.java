package com.svadhan.collection.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Data
public class LoanEmiDues {
    @JsonProperty("LoanId")
    private String loanId;
    @JsonProperty("CustomerName") 
    private String customerName;
    @JsonProperty("LoanOs") 
    private Double loanOs;
    @JsonProperty("DPD") 
    private Double dPD;
    @JsonProperty("DueAmount") 
    private Double dueAmount;
    @JsonProperty("EMIAmount") 
    private Double eMIAmount;
    @JsonProperty("EMIDate") 
    private String eMIDate;
    @JsonProperty("status")
    private int status;
    @JsonProperty("message")
    private String message;
    public LocalDate getEMIDate(){
        return LocalDate.parse(this.eMIDate, DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"));
    }
}

