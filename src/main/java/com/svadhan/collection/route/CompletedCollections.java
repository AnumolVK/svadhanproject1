package com.svadhan.collection.route;

import com.svadhan.collection.model.response.CollectionDetailsResponse;
import com.svadhan.collection.service.CollectionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CompletedCollections implements Function<Long, ResponseEntity<CollectionDetailsResponse>> {


    private final CollectionsService collectionsService;

    public CompletedCollections(CollectionsService collectionsService) {
        this.collectionsService = collectionsService;
    }


    @Override
    public ResponseEntity<CollectionDetailsResponse> apply(Long agentId) {
        CollectionDetailsResponse collectionDetailsResponse = collectionsService.getCompletedCollections(agentId);
        return ResponseEntity.ok(collectionDetailsResponse);
    }
}
