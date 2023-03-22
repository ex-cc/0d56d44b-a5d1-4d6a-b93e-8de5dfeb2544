package com.kakaobank.place.search.service.event;

import com.kakaobank.place.search.infrastructure.repository.H2KeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class EventHandler {
    private static final Marker MAKER = MarkerFactory.getMarker("EVENT");
    private final H2KeywordRepository repository;

    public EventHandler(H2KeywordRepository repository) {
        this.repository = repository;
    }

    @EventListener(SearchedEvent.class)
    @Async
    @Transactional
    public void searched(SearchedEvent event) {
        log.info(MAKER, "Event received. eventType={}, keyword={}", event.getClass().getName(), event.keyword());
        repository.incrementCountWithUpsertAndPessimisticLock(event.keyword());
    }

    @EventListener(SearchFailedEvent.class)
    @Async
    public void monitor(SearchFailedEvent event) {
        log.error(MAKER, "[Warn] Search failed. keyword={}, repository={}", event.keyword(), event.supplier());
    }
}
