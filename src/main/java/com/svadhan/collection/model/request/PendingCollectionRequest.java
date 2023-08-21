package com.svadhan.collection.model.request;

import lombok.Data;

@Data
public class PendingCollectionRequest {
    private Long agentId;
    private String sortOrder;

}
