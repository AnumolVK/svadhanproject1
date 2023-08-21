package com.svadhan.collection.service;

import com.svadhan.collection.model.request.PendingCollectionRequest;
import com.svadhan.collection.model.response.PendingCollectionResponse;

public interface PendingCollectionsService {


    PendingCollectionResponse getPendingCollections(PendingCollectionRequest pendingCollectionRequest);

}
