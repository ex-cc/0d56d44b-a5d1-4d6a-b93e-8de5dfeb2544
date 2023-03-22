package com.kakaobank.place.search.infrastructure.repository;

import com.kakaobank.place.search.domain.Place;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Repository("httpNaverRepository")
public class HttpNaverPlaceRepository extends AbstractHttpDefaultPlaceRepository<HttpNaverPlaceRepository.NaverResponse> {

    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final String url;
    private final String clientId;
    private final String secret;
    private final Integer display;
    private final Integer start;

    public HttpNaverPlaceRepository(RestTemplate restTemplate,
                                    ApplicationEventPublisher eventPublisher,
                                    @Value("${external.search-api.naver.url}") String url,
                                    @Value("${external.search-api.naver.client-id}") String clientId,
                                    @Value("${external.search-api.naver.secret}") String secret,
                                    @Value("${external.search-api.naver.display}") Integer display,
                                    @Value("${external.search-api.naver.start}") Integer start) {
        this.restTemplate = restTemplate;
        this.eventPublisher = eventPublisher;
        this.url = url;
        this.clientId = clientId;
        this.secret = secret;
        this.display = display;
        this.start = start;
    }

    @Override
    public RestTemplate restTemplate() {
        return this.restTemplate;
    }

    @Override
    ApplicationEventPublisher eventPublisher() {
        return this.eventPublisher;
    }

    @Override
    ParameterizedTypeReference<NaverResponse> getResponseType() {
        return new ParameterizedTypeReference<>(){};
    }

    @Override
    List<Place> placesFromResponse(NaverResponse response) {
        return response.getItems().stream()
                .map(item -> new Place(item.getTitle(), item.getAddress()))
                .toList();
    }

    @Override
    URI getUri(String keyword) {
        return UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("query", keyword)
                .queryParam("display", display)
                .queryParam("start", start)
                .build()
                .toUri();
    }

    @Override
    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", secret);
        return headers;
    }

    @Getter
    @Setter
    @ToString
    static class NaverResponse {
        private List<Item> items;

        @Getter
        @Setter
        @ToString
        static class Item {
            private String title;
            private String address;
            private Integer mapx;
            private Integer mapy;
        }
    }
}
