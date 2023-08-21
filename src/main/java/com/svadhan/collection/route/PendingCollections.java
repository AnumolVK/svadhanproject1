package com.svadhan.collection.route;

import com.svadhan.collection.model.request.PendingCollectionRequest;
import com.svadhan.collection.model.response.PendingCollectionResponse;
import com.svadhan.collection.service.PendingCollectionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PendingCollections implements Function<PendingCollectionRequest, ResponseEntity<PendingCollectionResponse>> {


    private final PendingCollectionsService pendingCollectionsService;

    public PendingCollections(PendingCollectionsService pendingCollectionsService) {

        this.pendingCollectionsService = pendingCollectionsService;
    }

    @Override
    public ResponseEntity<PendingCollectionResponse> apply(PendingCollectionRequest pendingCollectionRequest) {
        PendingCollectionResponse pendingCollectionResponse = pendingCollectionsService.getPendingCollections(pendingCollectionRequest);
        return ResponseEntity.ok(pendingCollectionResponse);
    }
}
