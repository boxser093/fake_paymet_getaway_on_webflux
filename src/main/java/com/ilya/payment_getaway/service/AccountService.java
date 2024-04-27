package com.ilya.payment_getaway.service;

import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.Currency;
import com.ilya.payment_getaway.entity.Merchant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService extends GenericService<AccountMerchant, Long> {
    Mono<AccountMerchant> findByMerchantIdAndCurrency(Long id, Currency currency);
    Flux<AccountMerchant> findAllByMerchantId (Long merchantId);
}
