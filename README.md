# Assignment - Place Search Service
***
"장소 검색 서비스" 를 위한 서버 프로그램
## 목록
* [사용한 기술들](#사용한기술들)
* [서버 실행](#서버-실행)
* [API Test](#api-test)
* [구현 및 시스템 고려사항](#구현-및-시스템-고려사항)
* [개선 사항](#개선-사항)

## 사용한 기술들
***
* Spring Boot v3.0.4
* JDK 17
* Maven
* Lombok
  * Logging 및 Bean 구현에 필요한 메소드를 자동화 하기 위해 사용합니다.
* H2
  * 키워드 및 검색 회수를 저장/조회하기 위한 용도로 사용합니다.
* Spring Retry
  * 검색 제공자 API 사용 시 장애가 발생 할 경우 재시도를 하기 위해 사용합니다.

## 서버 실행
***
Maven 기반 프로젝트로 다음과 같이 서버를 실행합니다.
```shell
$ ./mvnw spring-boot:run
```

## API Test
***
## 1. 장소 검색
```shell
curl --location 'http://localhost:8080/v1/places?q=kakao' \
--header 'Accept: application/json'
```
```json
200 OK.

{ 
  "places": [
    {
      "title": "카카오",
      "address": "경기 성남시 분당구 백현동 532"
    },
    {
      "title": "카카오 스페이스닷원",
      "address": "제주특별자치도 제주시 영평동 2181"
    },
    ...
  ]
}
```

## 2. 검색 키워드 목록 조회하기 (최대 10개)
```shell
curl --location 'http://localhost:8080/v1/keywords' \
--header 'Accept: application/json'
```
```json
{
"keywords": [
{
  "name": "카카오뱅크",
  "count": 100
}
...
]
}
```
IntelliJ 에서 테스트 시용할 수 있는 HTTP Request 파일도 다음 위치에 있습니다.

`http/Test.http`
```http request
### 장소 검색하기
GET http://localhost:8080/v1/places?q=kakaobank
Accept: application/json

### 키워드 목록 조회하기
GET http://localhost:8080/v1/keywords
Accept: application/json

```


## 구현 및 시스템 고려사항
***
### 1. 동일 업체 판단 기준
검색된 결과내에서 업체의 동일 여부 판단은 이름(Kakao의 경우 place_name, Naver의 경우 title)을 기준으로 tag 및 공백(좌/우) 을 제거하여 비교를 했습니다.

다음과 같이 Domain object 의 equal 메소드에 적용하여 비교 및 정렬 시 사용을 하고 있습니다.
```java
public class Place {
    private String title;
    private String address;
    ...
    public String getTitleWithNoTag() {
        return this.title.replaceAll("<[^>]*>", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return this.getTitleWithNoTag().trim().equals(place.getTitleWithNoTag().trim())
                && this.address.equals(place.getAddress());
    }
}

//Test
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

```

### 2. 카카오, 네이버 등 검색 API 제공자의 “다양한” 장애 및 연동 오류 발생 상황에 대한 고려
검색 API 를 사용하여 장애가 발생 시 [Spring Retry](https://github.com/spring-projects/spring-retry) 를 사용하여 재시도를 하고 정성 처리가 안 될 경우 기본 결과(없음)를 전달하도록 했습니다.
```java
@Retryable(maxAttempts = 3, recover = "recover", backoff = @Backoff(delay = 2000), listeners = {"defaultRetryListenerSupport"})
@Override
public List<Place> search(final String keyword) {
   ResponseEntity<T> response = restTemplate().exchange(
        getUri(keyword),
        HttpMethod.GET,
        getRequestEntity(),
        getResponseType());
        ...

@Recover
public List<Place> recover(RestClientException exception, String keyword) {
   log.debug("Recovered called. exception={}, message={}", exception.getClass().getName(), exception.getMessage());
   eventPublisher().publishEvent(SearchFailedEvent.of(keyword, getClass().getName()));
   return List.of();
}
```
추가로, retry 이후에 정상 연동이 안 될 경우에는 오류 사항을 별도로 처리할 수 있도록 Spring Event 를 활용합니다.
해당 Event 를 가지고 별도의 채널(Email, Messenger 등) 을 통해 모니터링를 해볼 수 있습니다.
```java
@Recover
public List<Place> recover(RestClientException exception, String keyword) {
   log.debug("Recovered called. exception={}, message={}", exception.getClass().getName(), exception.getMessage());
   eventPublisher().publishEvent(SearchFailedEvent.of(keyword, getClass().getName()));
   return List.of();
}
        ...
@EventListener(SearchFailedEvent.class)
@Async
public void monitor(SearchFailedEvent event) {
   log.error(MAKER, "[Warn] Search failed. keyword={}, repository={}", event.keyword(), event.supplier());
}
```

### 3. 동시성 이슈가 발생할 수 있는 부분을 염두에 둔 설계 및 구현
사용자가 검색한 키워드 및 회수를 제공하기 위해 다음과 같은 구조의 DB Table 을 사용합니다. (여기서는 H2 Database 를 사용했습니다.)
```sql
create table keyword (
  name varchar(255) not null,
  count integer,
  version integer,
  primary key (name)
)
```
특정 키워드의 검색이 대량 발생 할 경우 동시성 이슈를 고려하여 데이터 변경 시(검색 회수 +1) Lock 을 사용합니다.
```sql
   @Modifying
    @Query(value = "MERGE INTO KEYWORD t " +
            "USING (VALUES (:name, 1)) v(name, count) " +
            "ON t.NAME = v.NAME " +
            "WHEN MATCHED THEN UPDATE SET count = v.count + 1 " +
            "WHEN NOT MATCHED THEN INSERT (name, count) VALUES (v.name, v.count)", nativeQuery = true)
    void incrementCountWithUpsertAndPessimisticLock(@Param("name") String name);
```
쿼리는 사용하는 H2 Database 에서 제공하는 Merge 를 사용 했습니다. (사용하는 Database 에 따라서 쿼리는 변경할 수 있겠습니다.)
추가적으로 검색어의 회수를 적용하는 방법에 대해서는 CQRS Pattern 을 적용하는 방법도 고려해볼 수 있겠습니다.
* 사용자의 검색요청을 저장. 예) '검색키워드', '검색일시'
* 키워드 검색 시 해당 키워드 검색 로그를 취합하여 검색 회수를 계산함
* 키워드 검색이 많을 경우 취합하는 로그의 사이즈가 커지기 때문에 일정 시간이 지난 로그들에 대해서는 계산을 미리하여 합산하는 형태로 사용하도록 함

### 4. 대용량 트래픽 처리를 위한 반응성(Low Latency), 확장성(Scalability), 가용성(Availability)을 높이기 위한 고려
검색 시 동일 키워드에 대한 요청을 지속적으로 검색 API 제공자에게 전달하는 것은 다음과 같은 문제를 만들 수 있겠습니다.
* 동일 검색 키워드의 결과가 동일한 경우에 필요없는 요청을 검색 API 제공자에게 전달하게 됨
* 검색 API 사용이 유료일 경우 과다 비용이 발생할 수 있음
* 검색 API 제공자로 부터 전달받은 시간이 소요되어 사용자의 응답시간의 지연이 발생 할 수 있음 

위와 같은 문제점을 해결하기 위한 방법으로 **Cache** 를 적용하였으며 방식은 [Cache-Aside](https://learn.microsoft.com/en-us/azure/architecture/patterns/cache-aside) 를 사용 했습니다.
Cache 는 기능 구현의 목적으로 간단한 방식을 적용했으며 Redis 와 같은 다른 솔루션도 적용이 가능하겠습니다.
```java
//Interface
public interface Cacheable {
    void put(String key, List<Place> value);
    List<Place> get(String key);
    boolean hasKey(String key);
}
    ...
//Simple implementation
@Slf4j
@Component("simpleMemoryCache")
public class MemoryCache implements Cacheable {
    ...

//Service
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
```
```java
2023-03-22T16:10:59.603+09:00  INFO 99547 --- [nio-8080-exec-2] c.k.place.search.service.SearchService   : A cache miss. keyword=kakao
2023-03-22T16:11:11.837+09:00  INFO 99547 --- [nio-8080-exec-3] c.k.place.search.service.SearchService   : A cache hit. keyword=kakao
```
Cache 는 메모리 사용량을 고려해서 LRU 형태로 적용을 했으며 기본 사이즈(10) 또는 특정 사이즈를 설정하게 했습니다.

```java
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
```

### 5. 구글 장소 검색 등 새로운 검색 API 제공자의 추가 시 변경 영역 최소화에 대한 고려
현재는 검색 API 제공자의 경우 HTTP 를 통한 API 를 제공한다는 전제하에 다음과 같이 추상화한 클래스를 확장하여 사용하고 있습니다.
```java
public abstract class AbstractHttpDefaultPlaceRepository<T> implements PlaceRepository {

    ...
    abstract RestTemplate restTemplate();

    abstract ParameterizedTypeReference<T> getResponseType();

    abstract List<Place> placesFromResponse(T response);

    abstract URI getUri(final String keyword);

    abstract HttpHeaders getHeaders();
}
```
검색 제공자 마다 다른 URL 및 인증/응답을 처리하기 위해 위오 같이 추상화 메소드를 분리해뒀습니다.
해당 클래스를 추가 후에는 검색을 처리하는 Service 에서 필요한 정렬 조건을 추가한 후 적용하도록 구성을 했습니다.
```java

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
        
```
다만, 제약사항은 정렬 책을 수행하고 있는 OrderStrategy.order() 메소드의 경우 2개의 검색 제공자만 적용하도록 되어 있으니 검색 제공자가 추가될 경우에는 수정 또는 메소드 추가가 필요합니다.


## 개선 사항
1. 검색 제공자의 장애가 발생하는 경우 Retry 후 결과 없음으로 검색 결과 전달
   * 사용자의 검색결과를 저장했다가 장애 발생 시 해당 키워드의 검색결과가 있는 경우 제공해주는 방식 적용
2. 현재는 검색 제공자를 2개로 제한하여 지정이 되어 있음
   * 2개 이상 다수의 검색 제공자를 연동하는 경우에도 적용할 수 있도록 구조 변경이 필요 함