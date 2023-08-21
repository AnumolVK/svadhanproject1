package com.svadhan.collection.route;

import com.svadhan.collection.service.impl.LenderApi7service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class LenderAPI7Scheduler implements Supplier<ResponseEntity<?>> {
    private final LenderApi7service lenderApi7service;
    @Override
    public ResponseEntity<?> get() {
        lenderApi7service.processLenderAPI7();
        return ResponseEntity.ok("Success");
    }
}
