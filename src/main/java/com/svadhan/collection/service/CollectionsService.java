package com.svadhan.collection.service;

import com.svadhan.collection.model.response.CollectionDetailsResponse;

public interface CollectionsService {


    void confirmCollection();

    CollectionDetailsResponse getCompletedCollections(Long agentId);
}
