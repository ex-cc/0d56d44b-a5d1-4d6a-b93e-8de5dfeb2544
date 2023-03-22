package com.kakaobank.place.search.service;

import com.kakaobank.place.search.domain.Place;
import com.kakaobank.place.search.infrastructure.cache.Cacheable;
import com.kakaobank.place.search.repository.PlaceRepository;
import com.kakaobank.place.search.service.event.SearchedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SearchService {
    private final PlaceRepository kakaoPlaceRepository;
    private final PlaceRepository naverPlaceRepository;
    private final OrderStrategy orderStrategy;
    private final Cacheable cacheable;
    private final ApplicationEventPublisher eventPublisher;

    public SearchService(@Qualifier("httpKakaoRepository") PlaceRepository kakaoPlaceRepository,
                         @Qualifier("httpNaverRepository") PlaceRepository naverPlaceRepository,
                         @Qualifier("kakaoFirst") OrderStrategy orderStrategy,
                         @Qualifier("simpleMemoryCache") Cacheable cacheable,
                         ApplicationEventPublisher eventPublisher) {
        this.kakaoPlaceRepository = kakaoPlaceRepository;
        this.naverPlaceRepository = naverPlaceRepository;
        this.orderStrategy = orderStrategy;
        this.cacheable = cacheable;
        this.eventPublisher = eventPublisher;
    }

    public List<Place> search(final String keyword) {

        eventPublisher.publishEvent(SearchedEvent.of(keyword));

        if (cacheable.hasKey(keyword)) {
            log.info("A cache hit. keyword={}", keyword);
            return cacheable.get(keyword);
        }

        List<Place> ordered =  orderStrategy.order(
            kakaoPlaceRepository.search(keyword),
            naverPlaceRepository.search(keyword)
        );

        log.info("A cache miss. keyword={}", keyword);
        cacheable.put(keyword, ordered);

        return ordered;
    }

}
