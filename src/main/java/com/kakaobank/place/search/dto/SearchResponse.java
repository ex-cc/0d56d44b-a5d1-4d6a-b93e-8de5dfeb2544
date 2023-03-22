package com.kakaobank.place.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchResponse {
   private List<Place> places;

   @Getter
   @Builder
    public static class Place {
        private String title;
        private String address;
    }
}
