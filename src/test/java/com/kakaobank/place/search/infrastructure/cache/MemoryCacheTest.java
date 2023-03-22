package com.kakaobank.place.search.infrastructure.cache;

import com.kakaobank.place.search.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Simple Memory Cache 테스트")
class MemoryCacheTest {

    @DisplayName("Cache 사이즈 초과 시 제일 오래된 것 삭제")
    @Test
    void testOverMaxSize() {
        Cacheable cache = new MemoryCache(5);
        List.of("1", "2", "3", "4", "5", "6").forEach(it -> cache.put(it, List.of(new Place())));
        assertThat(cache.hasKey("1")).isFalse();
    }

    @DisplayName("LRU 테스트")
    @Test
    void testLRU() {
        Cacheable cache = new MemoryCache(3);
        List.of("1", "2", "3").forEach(it -> cache.put(it, List.of(new Place())));
        cache.get("1");
        cache.put("4", List.of(new Place()));

        assertThat(cache.hasKey("1")).isTrue();
        assertThat(cache.hasKey("2")).isFalse();
        assertThat(cache.hasKey("4")).isTrue();
    }
}