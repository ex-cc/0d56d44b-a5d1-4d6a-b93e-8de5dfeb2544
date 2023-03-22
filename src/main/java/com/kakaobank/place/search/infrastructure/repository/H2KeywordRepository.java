package com.kakaobank.place.search.infrastructure.repository;

import com.kakaobank.place.search.domain.Keyword;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository("H2KeywordRepository")
public interface H2KeywordRepository extends JpaRepository<Keyword, String> {
    List<Keyword> findTop10ByOrderByCountDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM Keyword k WHERE k.name = :name")
    Optional<Keyword> findByNameForUpdate(final String name);

    @Modifying
    @Query(value = "MERGE INTO KEYWORD t " +
            "USING (VALUES (:name, 1)) v(name, count) " +
            "ON t.NAME = v.NAME " +
            "WHEN MATCHED THEN UPDATE SET count = v.count + 1 " +
            "WHEN NOT MATCHED THEN INSERT (name, count) VALUES (v.name, v.count)", nativeQuery = true)
    void incrementCountWithUpsertAndPessimisticLock(@Param("name") String name);
}
