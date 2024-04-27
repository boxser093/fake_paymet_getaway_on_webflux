package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.Currency;
import com.ilya.payment_getaway.repository.AccountMerchantRepository;
import com.ilya.payment_getaway.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountMerchantRepository accountMerchantRepository;


    @Override
    public Mono<AccountMerchant> findById(Long aLong) {
        log.info("IN AccountCardServiceImpl, findById -{}", aLong);
        return accountMerchantRepository.findById(aLong);
    }

    @Override
    public Mono<AccountMerchant> create(AccountMerchant accountMerchant) {
        log.info("IN AccountCardServiceImpl, create -{}", accountMerchant);
        return accountMerchantRepository.save(accountMerchant.toBuilder()
                        .createAt(LocalDateTime.now())
                        .build());
    }

    @Override
    public Mono<AccountMerchant> update(AccountMerchant accountMerchant) {
        log.info("IN AccountCardServiceImpl, update -{}", accountMerchant);
        return accountMerchantRepository.findById(accountMerchant.getId())
                .map(accountMerchant1 ->
                        accountMerchant1.toBuilder()
                                .merchantId(accountMerchant.getMerchantId())
                                .balance(accountMerchant.getBalance())
                                .currency(accountMerchant.getCurrency())
                                .updateAt(LocalDateTime.now())
                                .build())
                .flatMap(accountMerchantRepository::save);
    }


    @Override
    public Flux<AccountMerchant> findAll() {
        log.info("IN AccountCardServiceImpl, findAll");
        return accountMerchantRepository.findAll();
    }

    @Override
    public Mono<AccountMerchant> findByMerchantIdAndCurrency(Long id, Currency currency) {
        return accountMerchantRepository.findAccountMerchantByMerchantIdAndCurrency(id, currency);
    }

    @Override
    public Flux<AccountMerchant> findAllByMerchantId(Long merchantId) {
        return accountMerchantRepository.findAllByMerchantId(merchantId);
    }
}
