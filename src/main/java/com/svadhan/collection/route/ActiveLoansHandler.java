package com.svadhan.collection.route;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.svadhan.collection.CollectionApplication;
import com.svadhan.collection.model.response.LoanDetailsResponse;
import com.svadhan.collection.service.LoansService;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.function.adapter.aws.AWSCompanionAutoConfiguration;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

public class ActiveLoansHandler implements RequestHandler<Long, ResponseEntity<LoanDetailsResponse>> {

    LoansService loansService;

    public ActiveLoansHandler(){
        start();
    }

    private void start(){
        Class<?> startClass = CollectionApplication.class;
        String[] properties = new String[]{"--spring.cloud.function.web.export.enabled=false", "--spring.main.web-application-type=none"};
        ConfigurableApplicationContext context = ApplicationContextInitializer.class.isAssignableFrom(startClass) ? FunctionalSpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties) : SpringApplication.run(new Class[]{startClass, AWSCompanionAutoConfiguration.class}, properties);
        Environment environment = context.getEnvironment();
        this.loansService = context.getBean(LoansService.class);
    }

    @Override
    public ResponseEntity<LoanDetailsResponse> handleRequest(Long input, Context context) {
        return processInput(input);
    }

    private ResponseEntity<LoanDetailsResponse> processInput(Long input) {
        LoanDetailsResponse activeLoans = loansService.getActiveLoans(input);
        return ResponseEntity.ok(activeLoans);
    }
}
