package com.svadhan.collection.model.lender;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LastTransactions {
    @JsonProperty("date")
    public String date;
    @JsonProperty("transactionType")
    public String transactionType;
    @JsonProperty("narration")
    public String narration;
    @JsonProperty("amount")
    public int amount;
}