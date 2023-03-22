package com.kakaobank.place.search.service;

import com.kakaobank.place.search.domain.Place;

import java.util.List;

public interface OrderStrategy {

    List<Place> order(List<Place> list1, List<Place> list2);

}
