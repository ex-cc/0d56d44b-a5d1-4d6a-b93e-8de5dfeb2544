package com.kakaobank.place.search.repository;

import com.kakaobank.place.search.domain.Keyword;

import java.util.List;

public interface KeywordRepository {
    List<Keyword> findAll();
    void updateCount(Keyword keyword);
}
