package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.dto.AcceptResponse;
import com.ilya.payment_getaway.entity.*;
import com.ilya.payment_getaway.errorhandling.NoTransaction;
import com.ilya.payment_getaway.errorhandling.TransactionTopUtFailed;
import com.ilya.payment_getaway.errorhandling.TransactionWithdrawalFiled;
import com.ilya.payment_getaway.repository.*;
import com.ilya.payment_getaway.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService, CheckBalanceInterface {
    private final TransactionRepository transactionRepository;
    private final CustomerCardService customerCardService;
    private final CustomerService customerService;
    private final AccountService accountService;

    @Override
    public Mono<Transaction> findById(UUID uuid) {
        log.info("TransactionServiceImpl findById:{}", uuid);
        return transactionRepository.findById(uuid);
    }

    @Override
    public Mono<Transaction> findTransactionByIdAndMerchantId(Long merchantId, UUID transactionId, TransactionType transactionType) {
        log.info("TransactionServiceImpl findByIdAndMerchantId:{}", transactionId);
        return Mono.from(accountService.findAllByMerchantId(merchantId).flatMap(accountMerchant ->
                        transactionRepository.findAllByAccountId(accountMerchant.getId())
                                .filter(transaction -> transaction.getAccountId().equals(accountMerchant.getId())
                                        && transaction.getTransactionType().equals(transactionType)
                                        && transaction.getId().equals(transactionId))))
                .switchIfEmpty(Mono.error(() -> new NoTransaction("No find transaction for you id")));
    }

    @Override
    public Flux<Transaction> findAllInProgress(TransactionStatus transactionStatus) {
        return transactionRepository.findAllByTransactionStatus(transactionStatus);
    }

    @Override
    public Mono<AcceptResponse> topUp(Transaction transaction, Merchant merchant) {
        log.info("TransactionServiceImpl TOP_UP transaction begin with merchant ID - {}", merchant.getId());
        return customerService.findCustomerByFirstNameAndLastName(transaction.getCustomer().getFirstName(), transaction.getCustomer().getLastName())
                .switchIfEmpty(customerService.create(transaction.getCustomer()))
                .flatMap(customer -> customerCardService.findCustomerCardByCartNumber(transaction.getCustomerCard().getCartNumber())
                        .switchIfEmpty(customerCardService.create(transaction.getCustomerCard().toBuilder()
                                .currency(transaction.getCurrency())
                                .customerId(customer.getId())
                                .build()))
                        .flatMap(customerCard ->
                                accountService.findByMerchantIdAndCurrency(merchant.getId(), transaction.getCurrency())
                                        .flatMap(accountMerchant -> {
                                            if (customerCard.getBalance().equals(BigDecimal.ZERO)) {
                                                log.info("TransactionServiceImpl topUp not a successful operation for card:{}, customer:{}",
                                                        transaction.getCustomerCard(), transaction.getCustomer());
                                                return Mono.error(new TransactionTopUtFailed(customerCard.getCartNumber()));
                                            } else {
                                                log.info("TransactionServiceImpl topUp successful operation for card:{}, customer:{}",
                                                        transaction.getCustomerCard().getCartNumber(), transaction.getCustomer().getFirstName() + " "
                                                                + transaction.getCustomer().getLastName());
                                                return holdBalanceForTopUp(transaction, accountMerchant, customerCard);
                                            }
                                        })));
    }

    @Override
    public Mono<AcceptResponse> payout(Transaction transaction, Merchant merchant) {
        log.info("TransactionServiceImpl PAYOUT transaction - {}, merchant - {}", transaction, merchant);
        return customerService.findCustomerByFirstNameAndLastName(transaction.getCustomer().getFirstName(), transaction.getCustomer().getLastName())
                .switchIfEmpty(customerService.create(transaction.getCustomer()))
                .flatMap(customer -> customerCardService.findCustomerCardByCartNumber(transaction.getCustomerCard().getCartNumber())
                        .switchIfEmpty(customerCardService.create(transaction.getCustomerCard().toBuilder()
                                .currency(transaction.getCurrency())
                                .customerId(customer.getId())
                                .build()))
                        .flatMap(customerCard -> accountService.findByMerchantIdAndCurrency(merchant.getId(), transaction.getCurrency())
                                .flatMap(accountMerchant -> {
                                    if (checkBalanceForPayout(transaction, accountMerchant)) {
                                        log.info("TransactionServiceImpl payout successful operation for card:{}, customer:{}", transaction.getCustomerCard(), transaction.getCustomer());
                                        return holdBalanceForPayout(transaction, accountMerchant, customerCard);
                                    } else {
                                        log.info("TransactionServiceImpl topUp not a successful operation for card:{}, customer:{}", transaction.getCustomerCard(), transaction.getCustomer());
                                        return Mono.error(new TransactionWithdrawalFiled(accountMerchant.toString()));
                                    }
                                })));
    }

    @Override
    public Mono<Transaction> create(Transaction transaction) {
        log.info("TransactionServiceImpl create - {}", transaction);
        return transactionRepository.save(transaction.toBuilder()
                .createAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Transaction> update(Transaction transaction) {
        log.info("TransactionServiceImpl update - {}", transaction);
        return transactionRepository
                .findById(transaction.getId())
                .map(transaction1 -> transaction.toBuilder()
                        .updateAt(LocalDateTime.now())
                        .build())
                .flatMap(transactionRepository::save);
    }


    @Override
    public Flux<Transaction> findAll() {
        log.info("TransactionServiceImpl findAll");
        return transactionRepository.findAll();
    }

    @Override
    public Flux<Transaction> findAllByDateAndAccountId(Merchant merchant, String start, String exit, TransactionType transactionType) {
        log.info("TransactionServiceImpl findAllByAccountId - {}, start date:{}, and end:{}", merchant.getId(), start, exit);
        if (start == null | exit == null) {
            return accountService.findAllByMerchantId(merchant.getId())
                    .flatMap(x -> transactionRepository.findAllByAccountId(x.getMerchantId()).filter(transaction -> transaction.getTransactionType().equals(transactionType)))
                    .switchIfEmpty(Mono.error(() -> new NoTransaction(String.format("Merchant Id:%d", merchant.getId()))));
        } else {
            LocalDateTime startDateParse = LocalDateTime.parse(start);
            LocalDateTime endDateParse = LocalDateTime.parse(exit);
            return accountService.findAllByMerchantId(merchant.getId())
                    .flatMap(accountMerchant -> transactionRepository.findAllByAccountId(accountMerchant.getMerchantId())
                            .filter(transaction -> transaction.getTransactionType().equals(transactionType)
                                    && transaction.getCreateAt().isAfter(startDateParse)
                                    && transaction.getCreateAt().isBefore(endDateParse)))
                    .switchIfEmpty(Mono.error(() -> new NoTransaction(String.format("No find transaction for merchant id:%d, \n " +
                                    "and date between %s and %s", merchant.getId(), start, exit))));
        }
    }

    @Override
    public Mono<AcceptResponse> holdBalanceForTopUp(Transaction transaction, AccountMerchant accountMerchant, CustomerCard customerCard) {
        if (checkBalanceForTopUp(transaction, customerCard)) {
            return customerCardService.update(customerCard.toBuilder()
                            .balance(customerCard.getBalance().subtract(transaction.getAmount()))
                            .build())
                    .flatMap(customerCard1 -> accountService.update(accountMerchant.toBuilder()
                            .balance(accountMerchant.getBalance().add(transaction.getAmount()))
                            .build()))
                    .flatMap(accountMerchant1 -> transactionRepository.save(transaction.toBuilder()
                            .paymentMethod(transaction.getPaymentMethod())
                            .amount(transaction.getAmount())
                            .notificationUrl(transaction.getNotificationUrl())
                            .currency(transaction.getCurrency())
                            .transactionType(TransactionType.TOP_UP)
                            .transactionStatus(TransactionStatus.IN_PROGRESS)
                            .customerCardId(customerCard.getCustomerId())
                            .accountId(accountMerchant.getId())
                            .createAt(LocalDateTime.now())
                            .build())).flatMap(x -> Mono.just(new AcceptResponse(x.getId(), x.getTransactionStatus())));
        } else return Mono.error(new TransactionTopUtFailed(customerCard.getCartNumber()));
    }

    @Override
    public Mono<AcceptResponse> holdBalanceForPayout(Transaction transaction, AccountMerchant accountMerchant, CustomerCard customerCard) {
        if (checkBalanceForPayout(transaction, accountMerchant)) {
            return customerCardService.update(customerCard.toBuilder()
                            .balance(customerCard.getBalance().add(transaction.getAmount()))
                            .build())
                    .flatMap(customerCard1 -> accountService.update(accountMerchant.toBuilder()
                            .balance(accountMerchant.getBalance().subtract(transaction.getAmount()))
                            .build()))
                    .flatMap(accountMerchant1 -> transactionRepository.save(Transaction.builder()
                            .paymentMethod(transaction.getPaymentMethod())
                            .amount(transaction.getAmount())
                            .notificationUrl(transaction.getNotificationUrl())
                            .currency(transaction.getCurrency())
                            .transactionType(TransactionType.WITHDRAWAL)
                            .transactionStatus(TransactionStatus.IN_PROGRESS)
                            .customerCardId(customerCard.getCustomerId())
                            .accountId(accountMerchant.getId())
                            .createAt(LocalDateTime.now())
                            .build())).flatMap(x -> Mono.just(new AcceptResponse(x.getId(), x.getTransactionStatus())));
        } else return Mono.error(new TransactionWithdrawalFiled(accountMerchant.toString()));
    }

    @Override
    public boolean checkBalanceForTopUp(Transaction transaction, CustomerCard customerCard) {
        return customerCard.getBalance().compareTo(transaction.getAmount()) > 0;
    }

    @Override
    public boolean checkBalanceForPayout(Transaction transaction, AccountMerchant accountMerchant) {
        return accountMerchant.getBalance().compareTo(transaction.getAmount()) > 0;
    }
}
