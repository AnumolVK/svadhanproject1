package com.svadhan.collection.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingCollectionResponse {
    private Integer customersDue;
    private Integer loansDue;
    private Double amountsDue;
    private List<Map<String, Object>> pendingCustomerCollectionResponses;

}
