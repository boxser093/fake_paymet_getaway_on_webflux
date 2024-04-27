package com.ilya.payment_getaway.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericService<T, V> {
    Mono<T> findById(V v);

    Mono<T> create(T t);

    Mono<T> update(T t);

    Flux<T> findAll();
}
