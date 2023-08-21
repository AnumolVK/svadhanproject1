package com.svadhan.collection.route;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.svadhan.collection.CollectionApplication;
import com.svadhan.collection.model.request.PendingCollectionRequest;
import com.svadhan.collection.model.response.PendingCollectionResponse;
import com.svadhan.collection.service.PendingCollectionsService;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.function.adapter.aws.AWSCompanionAutoConfiguration;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

public class PendingCollectionsHandler implements RequestHandler<PendingCollectionRequest, ResponseEntity<PendingCollectionResponse>> {

    PendingCollectionsService pendingCollectionsService;

    public PendingCollectionsHandler(){
        start();
    }

    private void start(){
        Class<?> startClass = CollectionApplication.class;
        String[] properties = new String[]{"--spring.cloud.function.web.export.enabled=false", "--spring.main.web-application-type=none"};
        ConfigurableApplicationContext context = ApplicationContextInitializer.class.isAssignableFrom(startClass) ? FunctionalSpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties) : SpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties);
        Environment environment = context.getEnvironment();
        this.pendingCollectionsService = context.getBean(PendingCollectionsService.class);
    }

    @Override
    public ResponseEntity<PendingCollectionResponse> handleRequest(PendingCollectionRequest input, Context context) {
        return processInput(input);
    }

    private ResponseEntity<PendingCollectionResponse> processInput(PendingCollectionRequest input) {
        PendingCollectionResponse pendingCollectionResponse = pendingCollectionsService.getPendingCollections(input);
        return ResponseEntity.ok(pendingCollectionResponse);
    }
}
