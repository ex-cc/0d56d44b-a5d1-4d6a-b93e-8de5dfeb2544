package com.kakaobank.place.search.controller;

import com.kakaobank.place.search.domain.Place;
import com.kakaobank.place.search.dto.SearchResponse;
import com.kakaobank.place.search.mapper.PlaceDataMapper;
import com.kakaobank.place.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlaceController {

    private final PlaceDataMapper mapper;
    private final SearchService searchService;

    public PlaceController(PlaceDataMapper mapper, SearchService searchService) {
        this.mapper = mapper;
        this.searchService = searchService;
    }

    @GetMapping("/v1/places")
    public ResponseEntity<SearchResponse> search(@RequestParam("q") final String keyword) {
        final List<Place> places = searchService.search(keyword);
        final SearchResponse response = mapper.placesToSearchResponse(places);
        return ResponseEntity.ok(response);
    }
}
