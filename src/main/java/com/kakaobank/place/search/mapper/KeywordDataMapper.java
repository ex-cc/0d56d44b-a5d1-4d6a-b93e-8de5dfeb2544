package com.kakaobank.place.search.mapper;

import com.kakaobank.place.search.domain.Keyword;
import com.kakaobank.place.search.dto.KeywordResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeywordDataMapper {
    public KeywordResponse keywordsToKeywordResponse(final List<Keyword> keywords) {
        final List<KeywordResponse.Keyword> list = keywords.stream()
                .map(item ->
                        KeywordResponse.Keyword.builder()
                                .name(item.getName())
                                .count(item.getCount())
                                .build()
                )
                .toList();

        return KeywordResponse.builder()
                .keywords(list)
                .build();
    }
}
