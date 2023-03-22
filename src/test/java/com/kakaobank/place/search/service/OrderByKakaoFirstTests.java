package com.kakaobank.place.search.service;

import com.kakaobank.place.search.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("카카오 기준 정렬 조건 테스트")
class OrderByKakaoFirstTests {
    private final OrderStrategy order = new OrderByKakaoFirst();

    @DisplayName("정렬 테스트")
    @Test
    void testKaKaoFirst() {
        List<Place> ordered = order.order(
            List.of(
                new Place("A곱창"),
                new Place("B곱창"),
                new Place("C곱창"),
                new Place("D곱창")
            ),
            List.of(
                new Place("A곱창"),
                new Place("E곱창"),
                new Place("D곱창"),
                new Place("C곱창")
            ));

        List<Place> expected = List.of(
            new Place("A곱창"),
            new Place("C곱창"),
            new Place("D곱창"),
            new Place("B곱창"),
            new Place("E곱창")
        );

        assertThat(ordered).isEqualTo(expected);
    }

    @DisplayName("정렬 테스트")
    @Test
    void testKaKaoFirstAndExclude() {
        List<Place> ordered = order.order(
            List.of(
                new Place("카카오뱅크"),
                new Place("우리은행"),
                new Place("국민은행"),
                new Place("부산은행"),
                new Place("새마을금고")
            ),
            List.of(
                new Place("카카오뱅크"),
                new Place("부산은행"),
                new Place("하나은행"),
                new Place("국민은행"),
                new Place("기업은행")
        ));

        List<Place> expected = List.of(
            new Place("카카오뱅크"),
            new Place("국민은행"),
            new Place("부산은행"),
            new Place("우리은행"),
            new Place("새마을금고"),
            new Place("하나은행"),
            new Place("기업은행")
        );

        assertThat(ordered).isEqualTo(expected);
    }
}
