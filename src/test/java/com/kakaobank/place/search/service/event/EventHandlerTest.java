package com.kakaobank.place.search.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EventHandlerTest {

    @Autowired
    private EventHandler eventHandler;

    @DisplayName("동일 키워드로 검색이 대량 발생할 경우 정상적으로 검색회수가 반영되는 테스트")
    @Test
    void test() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger();
        int numberOfExecute = 50;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfExecute);

        for (int i = 0; i < numberOfExecute; i++) {
            service.execute(() -> {
                try {
                    eventHandler.searched(new SearchedEvent("keyword"));
                    successCount.getAndIncrement();
                } catch (ObjectOptimisticLockingFailureException ooLfe) {
                    System.out.println("collision");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                latch.countDown();
            });
        }
        latch.await();

        assertThat(successCount.get()).isEqualTo(50);
    }
}