package com.ilya.payment_getaway.service;

import com.ilya.payment_getaway.dto.AcceptResponse;
import com.ilya.payment_getaway.entity.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransactionService extends GenericService<Transaction, UUID> {
    Flux<Transaction> findAllInProgress(TransactionStatus transactionStatus);
    Mono<AcceptResponse> topUp(Transaction transaction, Merchant merchant);
    Mono<AcceptResponse> payout(Transaction transaction, Merchant merchant);
    Flux<Transaction> findAllByDateAndAccountId(Merchant merchant, String start, String end, TransactionType transactionType);
    Mono<Transaction> findTransactionByIdAndMerchantId(Long merchantId, UUID transactionId, TransactionType transactionType);
    Mono<AcceptResponse> holdBalanceForTopUp(Transaction transaction, AccountMerchant accountMerchant, CustomerCard customerCard);
    Mono<AcceptResponse> holdBalanceForPayout(Transaction transaction, AccountMerchant accountMerchant, CustomerCard customerCard);
}
