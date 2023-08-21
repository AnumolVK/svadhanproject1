package com.svadhan.collection.route;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.svadhan.collection.CollectionApplication;
import com.svadhan.collection.model.request.PendingCollectionRequest;
import com.svadhan.collection.model.response.CustomerTrustCircleResponse;
import com.svadhan.collection.model.response.PendingCollectionResponse;
import com.svadhan.collection.service.PendingCollectionsService;
import com.svadhan.collection.service.TrustCircleService;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.function.adapter.aws.AWSCompanionAutoConfiguration;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class TrustCircleDetailsHandler implements RequestHandler<Long, ResponseEntity<List<CustomerTrustCircleResponse>>> {

    TrustCircleService circleService;

    public TrustCircleDetailsHandler(){
        start();
    }

    private void start(){
        Class<?> startClass = CollectionApplication.class;
        String[] properties = new String[]{"--spring.cloud.function.web.export.enabled=false", "--spring.main.web-application-type=none"};
        ConfigurableApplicationContext context = ApplicationContextInitializer.class.isAssignableFrom(startClass) ? FunctionalSpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties) : SpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties);
        Environment environment = context.getEnvironment();
        this.circleService = context.getBean(TrustCircleService.class);
    }

    @Override
    public ResponseEntity<List<CustomerTrustCircleResponse>> handleRequest(Long input, Context context) {
        return processInput(input);
    }

    private ResponseEntity<List<CustomerTrustCircleResponse>> processInput(Long input) {
        List<CustomerTrustCircleResponse> trustCircleDetailsForCustomer = circleService.getTrustCircleDetails(input);
        return ResponseEntity.ok(trustCircleDetailsForCustomer);
    }
}
