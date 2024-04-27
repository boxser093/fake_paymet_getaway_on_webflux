package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.*;
import com.ilya.payment_getaway.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionStatusFinalizeSchedulerServiceImpl implements TransactionStatusService, CheckBalanceInterface {
    private final AccountService accountService;
    private final CustomerCardService customerCardService;
    private final TransactionService transactionService;
    private final WebhookService webhookService;
    public void startFinalize() {
        transactionService.findAllInProgress(TransactionStatus.IN_PROGRESS)
                .map(this::randomStatus).publishOn(Schedulers.boundedElastic())
                .flatMap(this::saveUnsuccessfulOperation)
                .flatMap(this::saveSuccessfulOperation)
                .flatMap(webhookService::toWebhook)
                .flatMap(webhookService::sendWebhook)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @Override
    public boolean checkBalanceForTopUp(Transaction transaction, CustomerCard customerCard) {
        return customerCard.getBalance().compareTo(transaction.getAmount()) > 0;
    }

    @Override
    public boolean checkBalanceForPayout(Transaction transaction, AccountMerchant accountMerchant) {
        return accountMerchant.getBalance().compareTo(transaction.getAmount()) > 0;
    }

    @Override
    public Transaction randomStatus(Transaction t) {
        int l = (int) Math.round(Math.random() * 10);
        if (l >= 8) {
            t.setTransactionStatus(TransactionStatus.FAILED);
            return t;
        } else
            t.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        return t;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Mono<Transaction> saveUnsuccessfulOperation(Transaction transaction) {
        if (transaction.getTransactionStatus().equals(TransactionStatus.FAILED)) {
            if (transaction.getTransactionType().equals(TransactionType.TOP_UP)) {
                return returnTheMoneyTopUp(transaction);
            } else return returnTheMoneyPayout(transaction);
        } else {
            return Mono.just(transaction);
        }
    }
    @Override
    public Mono<Transaction> returnTheMoneyTopUp(Transaction transaction) {
        return Mono.zip(customerCardService.findById(transaction.getCustomerCardId()), accountService.findByMerchantIdAndCurrency(transaction.getAccountId(), transaction.getCurrency()))
                .flatMap(zip -> {
                    if (checkBalanceForPayout(transaction, zip.getT2())) {
                        return customerCardService.update(zip.getT1().toBuilder()
                                        .balance(zip.getT1().getBalance().add(transaction.getAmount()))
                                        .build())
                                .flatMap(customerCard -> accountService.update(zip.getT2().toBuilder()
                                        .balance(zip.getT2().getBalance().subtract(transaction.getAmount()))
                                        .build()))
                                .flatMap(accountMerchant -> transactionService.update(transaction.toBuilder()
                                        .transactionStatus(TransactionStatus.FAILED)
                                        .build()));
                    } else return transactionService.update(transaction.toBuilder()
                            .transactionStatus(TransactionStatus.FAILED)
                            .build());
                });
    }
    @Override
    public Mono<Transaction> returnTheMoneyPayout(Transaction transaction) {
        return Mono.zip(customerCardService.findById(transaction.getCustomerCardId()), accountService.findByMerchantIdAndCurrency(transaction.getAccountId(), transaction.getCurrency()))
                .flatMap(zip -> {
                    if (checkBalanceForTopUp(transaction, zip.getT1())) {
                        return customerCardService.update(zip.getT1().toBuilder()
                                        .balance(zip.getT1().getBalance().subtract(transaction.getAmount()))
                                        .build())
                                .flatMap(customerCard -> accountService.update(zip.getT2().toBuilder()
                                        .balance(zip.getT2().getBalance().add(transaction.getAmount()))
                                        .build())).flatMap(accountMerchant -> transactionService.update(transaction));
                    } else return transactionService.update(transaction.toBuilder()
                            .transactionStatus(TransactionStatus.FAILED)
                            .build());
                });
    }
    @Override
    public Mono<Transaction> saveSuccessfulOperation(Transaction transaction){
        if(transaction.getTransactionStatus().equals(TransactionStatus.SUCCESSFUL)){
            return transactionService.update(transaction);
        } else return Mono.just(transaction);

    }
}
