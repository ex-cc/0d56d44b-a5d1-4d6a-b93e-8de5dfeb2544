package com.kakaobank.place.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class KeywordResponse {
    private List<Keyword> keywords;

    @Builder
    @Getter
    public static class Keyword {
        private String name;
        private Integer count;
    }
}