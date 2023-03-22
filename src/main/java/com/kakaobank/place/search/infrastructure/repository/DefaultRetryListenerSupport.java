package com.kakaobank.place.search.infrastructure.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultRetryListenerSupport extends RetryListenerSupport {

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.warn("Retry attempt {} for retryable method {} threw exception {}",
                context.getRetryCount(),
                context.getAttribute("context.name"),
                throwable.getClass().getName());
        super.onError(context, callback, throwable);
    }
}
