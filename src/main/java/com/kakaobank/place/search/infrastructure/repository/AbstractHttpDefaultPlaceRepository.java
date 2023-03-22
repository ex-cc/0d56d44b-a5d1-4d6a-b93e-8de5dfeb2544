package com.kakaobank.place.search.infrastructure.repository;

import com.kakaobank.place.search.domain.Place;
import com.kakaobank.place.search.repository.PlaceRepository;
import com.kakaobank.place.search.service.event.SearchFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
public abstract class AbstractHttpDefaultPlaceRepository<T> implements PlaceRepository {


    @Retryable(maxAttempts = 3, recover = "recover", backoff = @Backoff(delay = 2000), listeners = {"defaultRetryListenerSupport"})
    @Override
    public List<Place> search(final String keyword) {
        ResponseEntity<T> response = restTemplate().exchange(
                getUri(keyword),
                HttpMethod.GET,
                getRequestEntity(),
                getResponseType());

        log.debug("Status Code: {}", response.getStatusCode());
        log.debug("Response body: {}", response.getBody());

        return placesFromResponse(response.getBody());
    }

    @Recover
    public List<Place> recover(RestClientException exception, String keyword) {
        log.debug("Recovered called. exception={}, message={}", exception.getClass().getName(), exception.getMessage());
        eventPublisher().publishEvent(SearchFailedEvent.of(keyword, getClass().getName()));
        return List.of();
    }

    abstract RestTemplate restTemplate();
    abstract ApplicationEventPublisher eventPublisher();
    abstract ParameterizedTypeReference<T> getResponseType();
    abstract List<Place> placesFromResponse(T response);
    abstract URI getUri(final String keyword);
    abstract HttpHeaders getHeaders();

    private HttpEntity<String> getRequestEntity() {
        return  new HttpEntity<>(getHeaders());
    }
}
