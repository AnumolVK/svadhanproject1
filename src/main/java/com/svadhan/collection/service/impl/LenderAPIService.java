package com.svadhan.collection.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.svadhan.collection.exception.customexception.LenderNotAvailableException;
import com.svadhan.collection.model.api8.UpdateRepaymentResponse;
import com.svadhan.collection.model.lender.Api6Response;
import com.svadhan.collection.model.lender.Api6Root;
import com.svadhan.collection.model.response.LoanEmiDues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LenderAPIService {

    @Value("${svadhan.lender-api.base.url}")
    String lenderApiBaseUrl;

    public List<LoanEmiDues> lenderApi7() {
        List<LoanEmiDues> loanEmiDues = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = new HashMap<>();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        requestBody.put("transactionDate", date);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(lenderApiBaseUrl + "Api7", HttpMethod.POST, request, String.class);
        if (response.getStatusCode().value() == 200 || response.getStatusCode().value() == 201) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                loanEmiDues = mapper.readValue(response.getBody(), new TypeReference<List<LoanEmiDues>>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return loanEmiDues;
    }

    public UpdateRepaymentResponse lenderApi8UpdateRepayment(String requestBody, int counnt) {
        if (counnt >= 3)
            throw new LenderNotAvailableException("We are facing network issue");
        UpdateRepaymentResponse updateRepaymentResponse= new UpdateRepaymentResponse();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(lenderApiBaseUrl + "Api8", HttpMethod.POST, request, String.class);
            if (response.getStatusCode().value() == 200 || response.getStatusCode().value() == 201) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Map<String, Object> hashMap = mapper.readValue(response.getBody(), new TypeReference<HashMap<String, Object>>() {
                    });
                    Map<String, Object> api2Response = (Map<String, Object>) hashMap.get("api8Response");
                    updateRepaymentResponse = mapper.convertValue(api2Response, UpdateRepaymentResponse.class);
                } catch (Exception e) {
                    throw e;
                }
            }
            return updateRepaymentResponse;
        } catch (Exception e) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e1) {
            }
            counnt = counnt + 1;
            return lenderApi8UpdateRepayment(requestBody, counnt);
        }
    }

    public Api6Response getLoanRepaymentDetails(String loanId) {
        Api6Root api6Root = new Api6Root();
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("loanId", loanId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(lenderApiBaseUrl + "Api6", HttpMethod.POST, request, String.class);
        if (response.getStatusCode().value() == 200 || response.getStatusCode().value() == 201) {
            JsonMapper mapper = new JsonMapper();
            try {
                api6Root = mapper.readValue(response.getBody(), Api6Root.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return api6Root.getApi6Response();
    }
}
