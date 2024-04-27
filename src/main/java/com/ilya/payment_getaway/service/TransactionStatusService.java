package com.ilya.payment_getaway.service;

import com.ilya.payment_getaway.entity.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionStatusService {
    Transaction randomStatus(Transaction transaction);
    Mono<Transaction> returnTheMoneyTopUp(Transaction transaction);
    Mono<Transaction> returnTheMoneyPayout(Transaction transaction);
    Mono<Transaction> saveSuccessfulOperation(Transaction transaction);
}
