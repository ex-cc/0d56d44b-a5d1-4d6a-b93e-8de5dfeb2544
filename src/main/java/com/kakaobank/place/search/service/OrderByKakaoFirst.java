package com.kakaobank.place.search.service;

import com.kakaobank.place.search.domain.Place;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("kakaoFirst")
public class OrderByKakaoFirst implements OrderStrategy {

    @Override
    public List<Place> order(List<Place> kakaoPlaces, List<Place> naverPlaces) {

        // duplicated list
        List<Place> duplicatedPlaces = new ArrayList<>(kakaoPlaces);
        duplicatedPlaces.retainAll(naverPlaces);
        log.debug("Duplicated places: {}", duplicatedPlaces);

        // kakao only list
        List<Place> kakaoOnlyPlaces = new ArrayList<>(kakaoPlaces);
        kakaoOnlyPlaces.removeAll(duplicatedPlaces);
        log.debug("Kakao only places: {}", kakaoOnlyPlaces);

        // naver only list
        List<Place> naverOnlyPlaces = new ArrayList<>(naverPlaces);
        naverOnlyPlaces.removeAll(duplicatedPlaces);
        log.debug("Naver only places: {}", naverOnlyPlaces);

        // concat all list
        List<Place> ordered = new ArrayList<>();
        ordered.addAll(duplicatedPlaces);
        ordered.addAll(kakaoOnlyPlaces);
        ordered.addAll(naverOnlyPlaces);

        // return
        return ordered;
    }

}
