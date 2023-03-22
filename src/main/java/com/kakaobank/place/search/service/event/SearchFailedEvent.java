package com.kakaobank.place.search.service.event;

public record SearchFailedEvent(String keyword, String supplier) {

    public static SearchFailedEvent of(String keyword, String supplier) {
        return new SearchFailedEvent(keyword, supplier);
    }
}
