package com.kakaobank.place.search.service;

import com.kakaobank.place.search.domain.Keyword;
import com.kakaobank.place.search.infrastructure.repository.H2KeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class KeywordService {
    private final H2KeywordRepository repository;

    public KeywordService(H2KeywordRepository repository) {
        this.repository = repository;
    }

    public List<Keyword> list() {
        return repository.findTop10ByOrderByCountDesc();
    }
}
