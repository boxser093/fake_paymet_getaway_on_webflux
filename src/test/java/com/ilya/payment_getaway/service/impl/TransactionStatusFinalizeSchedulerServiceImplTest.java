package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.*;
import com.ilya.payment_getaway.service.*;
import com.ilya.payment_getaway.util.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionStatusFinalizeSchedulerServiceImplTest {
    @Mock
    private AccountService accountService;
    @Mock
    private CustomerCardService customerCardService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private WebhookService webhookService;
    @InjectMocks
    private TransactionStatusFinalizeSchedulerServiceImpl transactionStatusFinalizeSchedulerService;

    @Test
    public void startFinalize() {
        Transaction transaction1 = DateUtils.getTransactionInProgress1();
        Transaction transaction2 = DateUtils.getTransactionInProgress2();
        when(transactionService.findAllInProgress(any())).thenReturn(Flux.just(transaction1, transaction2));

        transactionStatusFinalizeSchedulerService.startFinalize();

        verify(transactionService, times(1)).findAllInProgress(any());
        StepVerifier.create(transactionService.findAllInProgress(any()))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void saveUnsuccessfulOperation() {
        //given
        Transaction transaction = DateUtils.getTransactionTopUpFiled();
        AccountMerchant accountMerchant = DateUtils.getAccountMerchantJimCarrey();
        CustomerCard customerCard = DateUtils.getCustomerCardJohnCena();

        //when
        when(customerCardService.findById(any())).thenReturn(Mono.just(customerCard));
        when(accountService.findByMerchantIdAndCurrency(any(), any())).thenReturn(Mono.just(accountMerchant));
        when(customerCardService.update(any(CustomerCard.class))).thenReturn(Mono.just(customerCard));
        when(accountService.update(any(AccountMerchant.class))).thenReturn(Mono.just(accountMerchant));
        when(transactionService.update(any(Transaction.class))).thenReturn(Mono.just(transaction.toBuilder()
                .notificationUrl("TEST")
                .build()));
        ///then
        StepVerifier.create(transactionStatusFinalizeSchedulerService.saveUnsuccessfulOperation(transaction))
                .expectNextMatches(transaction1 -> transaction1.getId().equals(transaction.getId())
                        && transaction1.getNotificationUrl().equalsIgnoreCase("test"))
                .expectComplete()
                .verify();
    }

    @Test
    void returnTheMoneyTopUp() {
        //given
        Transaction transaction = DateUtils.getTransactionTopUpFiled();
        AccountMerchant accountMerchant = DateUtils.getAccountMerchantJimCarrey();
        CustomerCard customerCard = DateUtils.getCustomerCardJohnCena();
        //when
        when(customerCardService.findById(any())).thenReturn(Mono.just(customerCard));
        when(accountService.findByMerchantIdAndCurrency(any(), any())).thenReturn(Mono.just(accountMerchant));
        when(customerCardService.update(any(CustomerCard.class))).thenReturn(Mono.just(customerCard));
        when(accountService.update(any(AccountMerchant.class))).thenReturn(Mono.just(accountMerchant));
        when(transactionService.update(any(Transaction.class))).thenReturn(Mono.just(transaction.toBuilder()
                .transactionStatus(TransactionStatus.FAILED)
                .build()));
        //then
        StepVerifier.create(transactionStatusFinalizeSchedulerService.returnTheMoneyTopUp(transaction))
                .expectNextMatches(transaction1 -> transaction1.getId().equals(transaction.getId())
                        && transaction1.getTransactionStatus().equals(TransactionStatus.FAILED))
                .expectComplete()
                .verify();
    }

    @Test
    void returnTheMoneyPayout() {
        //given
        Transaction transaction = DateUtils.getTransactionTopUpFiled().toBuilder()
                .transactionType(TransactionType.WITHDRAWAL)
                .build();
        AccountMerchant accountMerchant = DateUtils.getAccountMerchantJimCarrey();
        CustomerCard customerCard = DateUtils.getCustomerCardJohnCena();
        //when
        when(customerCardService.findById(any())).thenReturn(Mono.just(customerCard));
        when(accountService.findByMerchantIdAndCurrency(any(), any())).thenReturn(Mono.just(accountMerchant));
        when(customerCardService.update(any(CustomerCard.class))).thenReturn(Mono.just(customerCard));
        when(accountService.update(any(AccountMerchant.class))).thenReturn(Mono.just(accountMerchant));
        when(transactionService.update(any(Transaction.class))).thenReturn(Mono.just(transaction.toBuilder()
                .transactionStatus(TransactionStatus.FAILED)
                .build()));
        //then
        StepVerifier.create(transactionStatusFinalizeSchedulerService.returnTheMoneyPayout(transaction))
                .expectNextMatches(transaction1 -> transaction1.getId().equals(transaction.getId())
                        && transaction1.getTransactionStatus().equals(TransactionStatus.FAILED))
                .expectComplete()
                .verify();
    }

    @Test
    void saveSuccessfulOperation() {
        //given
        Transaction transaction = DateUtils.getTransactionTopUpSuccess();
        //when
        when(transactionService.update(transaction)).thenReturn(Mono.just(transaction));
        //then
        StepVerifier.create(transactionStatusFinalizeSchedulerService.saveSuccessfulOperation(transaction))
                .expectNextMatches(transaction1 -> transaction1.getId().equals(transaction.getId())
                        && transaction1.getTransactionStatus().equals(TransactionStatus.SUCCESSFUL))
                .expectComplete()
                .verify();
    }

    @Test
    void randomStatus() {
        //given
        Transaction transactionTopUpInProgress = DateUtils.getTransactionTopUpInProgress();
        //then
        Assertions.assertTrue(transactionStatusFinalizeSchedulerService.randomStatus(transactionTopUpInProgress).getTransactionStatus() != TransactionStatus.IN_PROGRESS);
    }
}