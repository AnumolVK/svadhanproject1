package com.svadhan.collection.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DleStatus {
    PENDING("TC approval pending"),
    CONDUCT_DLE("Conduct DLE"),
    SCHEDULE_DLE("Schedule DLE"),
    COMPLETED ("DLE completed"),
    REJECTED("DLE rejected"),
    DATA_VERIFIED("Data Verified"),
    NEED_CORRECTION("Need correction");
    private final String value;

    DleStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
