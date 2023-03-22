package com.kakaobank.place.search.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("장소명 일치/불일치 테스트")
class PlaceTests {

    @DisplayName("동일 장소 테스트")
    @ParameterizedTest
    @MethodSource("samePlaces")
    void testSamePlace(Place samePlace) {
        assertThat(new Place("카카오")).isEqualTo(samePlace);
    }

    private static Stream<Place> samePlaces() {
        return Stream.of(
                new Place("카카오"),
                new Place("<b>카카오</b>"),
                new Place("<i><b>카카오</b></i>"),
                new Place(" 카카오 "),
                new Place(" <b>카카오</b> ")
        );
    }

    @DisplayName("다른 장소 테스트")
    @ParameterizedTest
    @MethodSource("anotherPlaces")
    void testDifferentPlace(Place anotherPlace) {
        assertThat(new Place("카카오")).isNotEqualTo(anotherPlace);
    }

    private static Stream<Place> anotherPlaces() {
        return Stream.of(
                new Place("카카오_"),
                new Place("카카 오"),
                new Place("<b>카카 오</b>"),
                new Place("<i><b>카카 오</b></i>"),
                new Place(" 카카_오 ")
        );
    }
}
