package com.kynsoft.share.core.domain.service;

import org.springframework.scheduling.annotation.Async;

@Async
public interface IEventService<T> {
    void create(T entity);

    void update(T entity);

    void delete(T entity);
}
