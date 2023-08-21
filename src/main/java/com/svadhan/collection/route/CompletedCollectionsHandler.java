package com.svadhan.collection.route;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.svadhan.collection.CollectionApplication;
import com.svadhan.collection.model.response.CollectionDetailsResponse;
import com.svadhan.collection.service.CollectionsService;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.function.adapter.aws.AWSCompanionAutoConfiguration;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

public class CompletedCollectionsHandler implements RequestHandler<Long, ResponseEntity<CollectionDetailsResponse>> {

    CollectionsService collectionsService;

    public CompletedCollectionsHandler(){
        start();
    }

    private void start(){
        Class<?> startClass = CollectionApplication.class;
        String[] properties = new String[]{"--spring.cloud.function.web.export.enabled=false", "--spring.main.web-application-type=none"};
        ConfigurableApplicationContext context = ApplicationContextInitializer.class.isAssignableFrom(startClass) ? FunctionalSpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties) : SpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties);
        Environment environment = context.getEnvironment();
        this.collectionsService = context.getBean(CollectionsService.class);
    }

    @Override
    public ResponseEntity<CollectionDetailsResponse> handleRequest(Long input, Context context) {
        return processInput(input);
    }

    private ResponseEntity<CollectionDetailsResponse> processInput(Long input) {
        CollectionDetailsResponse collectionDetailsResponse = collectionsService.getCompletedCollections(input);
        return ResponseEntity.ok(collectionDetailsResponse);
    }
}
