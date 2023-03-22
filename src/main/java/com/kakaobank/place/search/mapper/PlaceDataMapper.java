package com.kakaobank.place.search.mapper;

import com.kakaobank.place.search.domain.Place;
import com.kakaobank.place.search.dto.SearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlaceDataMapper {

    public SearchResponse placesToSearchResponse(final List<Place> places) {
        final List<SearchResponse.Place> list = places.stream()
                .map(place ->
                    SearchResponse.Place.builder()
                            .title(place.getTitle())
                            .address(place.getAddress())
                            .build()
                )
                .toList();

        return SearchResponse.builder()
                .places(list)
                .build();
    }

}
