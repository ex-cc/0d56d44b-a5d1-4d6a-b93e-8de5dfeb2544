package com.kakaobank.place.search.repository;

import com.kakaobank.place.search.domain.Place;

import java.util.List;

public interface PlaceRepository {
    List<Place> search(final String keyword);
}
