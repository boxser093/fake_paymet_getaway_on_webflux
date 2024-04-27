package com.ilya.payment_getaway.service;

import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.Merchant;
import reactor.core.publisher.Mono;

import java.util.List;


public interface MerchantService extends GenericService<Merchant, Long>{
    Mono<List<AccountMerchant>> getMerchantBalance(Long merchantId);

}
