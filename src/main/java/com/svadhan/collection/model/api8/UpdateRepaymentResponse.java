package com.svadhan.collection.model.api8;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateRepaymentResponse {
    @JsonProperty("loanId")
    private String loanId;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("message")
    private String message;
}