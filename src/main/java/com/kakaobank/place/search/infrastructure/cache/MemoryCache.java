package com.kakaobank.place.search.infrastructure.cache;

import com.kakaobank.place.search.domain.Place;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("simpleMemoryCache")
public class MemoryCache implements Cacheable {
    private final CustomMap<String , List<Place>> cache;
    private final int maxSize;

    public MemoryCache() {
        this.maxSize = 20;
        cache = new CustomMap<>(maxSize);
        log.info("Cache initialized. size={}", maxSize);
    }

    public MemoryCache(int maxSize) {
        this.maxSize = maxSize;
        cache = new CustomMap<>(maxSize);
        log.info("Cache initialized. size={}", maxSize);
    }
    @Override
    public void put(final String keyword, List<Place> places) {
        cache.put(keyword, places);
    }

    @Override
    public List<Place> get(String keyword) {
        if (cache.isEmpty() || !cache.containsKey(keyword)) {
            throw new NoCacheException(keyword);
        }

        return cache.get(keyword);
    }

    @Override
    public boolean hasKey(String key) {
        return cache.containsKey(key);
    }

    static class CustomMap<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;

        public CustomMap(int maxSize) {
            super(maxSize + 1, 1.0f, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxSize;
        }
    }

}
