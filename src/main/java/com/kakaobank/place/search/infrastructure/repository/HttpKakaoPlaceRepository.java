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

@Repository("httpKakaoRepository")
public class HttpKakaoPlaceRepository extends AbstractHttpDefaultPlaceRepository<HttpKakaoPlaceRepository.KakaoResponse> {

    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final String restApiKey;
    private final String url;
    private final Integer size;
    private final Integer page;

    public HttpKakaoPlaceRepository(RestTemplate restTemplate,
                                    ApplicationEventPublisher eventPublisher,
                                    @Value("${external.search-api.kakao.rest-api-key}") String restApiKey,
                                    @Value("${external.search-api.kakao.url}") String url,
                                    @Value("${external.search-api.kakao.size}") Integer size,
                                    @Value("${external.search-api.kakao.page}") Integer page) {
        this.restTemplate = restTemplate;
        this.eventPublisher = eventPublisher;
        this.restApiKey = restApiKey;
        this.url = url;
        this.size = size;
        this.page = page;
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
    ParameterizedTypeReference<KakaoResponse> getResponseType() {
        return new ParameterizedTypeReference<>(){};
    }

    @Override
    List<Place> placesFromResponse(KakaoResponse response) {
        return response.getDocuments().stream()
                .map(item -> new Place(item.getPlace_name(), item.getAddress_name()))
                .toList();
    }

    @Override
    URI getUri(String keyword) {
        return UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("query", keyword)
                .queryParam("size", size)
                .queryParam("page", page)
                .build()
                .toUri();
    }

    @Override
    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", Collections.singletonList("KakaoAK " + restApiKey));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    @Setter
    @Getter
    @ToString
    static class KakaoResponse {
        private List<Document> documents;

        @Setter
        @Getter
        @ToString
        static class Document {
            private String place_name;
            private String address_name;
        }
    }
}
