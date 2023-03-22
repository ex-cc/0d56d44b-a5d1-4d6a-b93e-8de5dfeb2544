package com.kakaobank.place.search.service.event;

public record SearchedEvent(String keyword) {

    public static SearchedEvent of(String keyword) {
        return new SearchedEvent(keyword);
    }

    @Override
    public String toString() {
        return "SearchedEvent{" +
                "keyword='" + keyword + '\'' +
                '}';
    }
}
