package com.svadhan.collection.model.lender;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties
public class Api6Response{
    @JsonProperty("loanOs")
    public int loanOs;
    @JsonProperty("isDue")
    public boolean isDue;
    @JsonProperty("dueAmount")
    public int dueAmount;
    @JsonProperty("nextEmiDue")
    public int nextEmiDue;
    @JsonProperty("nextEmiDate")
    public String nextEmiDate;
    @JsonProperty("last150Transactions")
    public ArrayList<LastTransactions> last150Transactions;
    @JsonProperty("Status")
    private Integer Status;
    @JsonProperty("Message")
    private String Message;
}

