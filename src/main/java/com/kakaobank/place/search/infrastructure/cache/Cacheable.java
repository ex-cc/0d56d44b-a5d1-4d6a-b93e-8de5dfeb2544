package com.kakaobank.place.search.infrastructure.cache;

import com.kakaobank.place.search.domain.Place;

import java.util.List;
import java.util.Optional;

public interface Cacheable {
    void put(String key, List<Place> value);
    List<Place> get(String key);
    boolean hasKey(String key);
}
