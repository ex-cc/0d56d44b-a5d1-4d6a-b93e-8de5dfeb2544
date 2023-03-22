package com.kakaobank.place.search.controller;

import com.kakaobank.place.search.domain.Keyword;
import com.kakaobank.place.search.dto.KeywordResponse;
import com.kakaobank.place.search.mapper.KeywordDataMapper;
import com.kakaobank.place.search.service.KeywordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KeywordController {

    private final KeywordService keywordService;
    private final KeywordDataMapper dataMapper;

    public KeywordController(KeywordService keywordService, KeywordDataMapper dataMapper) {
        this.keywordService = keywordService;
        this.dataMapper = dataMapper;
    }

    @GetMapping("/v1/keywords")
    public ResponseEntity<KeywordResponse> keywords() {
        List<Keyword> keywords = keywordService.list();
        KeywordResponse response = dataMapper.keywordsToKeywordResponse(keywords);
        return ResponseEntity.ok(response);
    }
}
