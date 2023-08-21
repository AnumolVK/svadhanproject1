package com.svadhan.collection.route;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.svadhan.collection.CollectionApplication;
import com.svadhan.collection.service.impl.LenderApi7service;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.function.adapter.aws.AWSCompanionAutoConfiguration;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

public class LenderAPI7SchedulerHandler implements RequestHandler<Long, ResponseEntity<?>> {

    LenderApi7service lenderApi7service;

    public LenderAPI7SchedulerHandler(){
        start();
    }

    private void start(){
        Class<?> startClass = CollectionApplication.class;
        String[] properties = new String[]{"--spring.cloud.function.web.export.enabled=false", "--spring.main.web-application-type=none"};
        ConfigurableApplicationContext context = ApplicationContextInitializer.class.isAssignableFrom(startClass) ? FunctionalSpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties) : SpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties);
        Environment environment = context.getEnvironment();
        this.lenderApi7service = context.getBean(LenderApi7service.class);
    }

    @Override
    public ResponseEntity<?> handleRequest(Long t, Context context) {
        return processInput(t);
    }

    private ResponseEntity<?> processInput(Long t) {
        lenderApi7service.processLenderAPI7();
        return ResponseEntity.ok("Success");
    }
}
