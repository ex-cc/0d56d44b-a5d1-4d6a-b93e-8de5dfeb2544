package com.kakaobank.place.search.infrastructure.repository;

import com.kakaobank.place.search.domain.Keyword;
import com.kakaobank.place.search.repository.KeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Repository
public class MemoryKeywordRepository implements KeywordRepository {
    Set<Keyword> set = new ConcurrentSkipListSet<>(
        //order by count desc
        (o1, o2) -> o2.getCount().compareTo(o1.getCount())
    );

    @Override
    public List<Keyword> findAll() {
        return new ArrayList<>(set);
    }

    @Override
    public void updateCount(Keyword keyword) {
        log.info("Keyword: {}", keyword);
    }
}
