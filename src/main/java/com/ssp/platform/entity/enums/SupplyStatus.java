package com.ssp.platform.entity.enums;

public enum SupplyStatus {
    WINNER("Победитель"),
    UNDER_REVIEW("На рассмотрении"),
    DENIED("Отклонено");

    private String message;

    SupplyStatus(String status) {
        message = status;
    }

    public String getMessage() {
        return message;
    }
}
