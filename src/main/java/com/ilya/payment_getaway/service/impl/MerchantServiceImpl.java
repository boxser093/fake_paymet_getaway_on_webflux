package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.Merchant;
import com.ilya.payment_getaway.repository.AccountMerchantRepository;
import com.ilya.payment_getaway.repository.MerchantRepository;
import com.ilya.payment_getaway.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    private final AccountMerchantRepository accountMerchantRepository;
    @Override
    public Mono<List<AccountMerchant>> getMerchantBalance(Long merchantId) {
        return accountMerchantRepository.findAll().filter(x -> x.getMerchantId().equals(merchantId)).collectList();
    }


    @Override
    public Mono<Merchant> findById(Long aLong) {
        log.info("IN MerchantServiceImpl, findById -{}", aLong);
        Mono<Merchant> byId = merchantRepository.findById(aLong);
        Mono<List<AccountMerchant>> listMono = accountMerchantRepository.findAll().filter(x -> x.getMerchantId().equals(aLong)).collectList();
        return Mono.zip(byId, listMono).map(t -> t.getT1().toBuilder()
                .accountMerchants(t.getT2())
                .build());
    }


    @Override
    public Mono<Merchant> create(Merchant merchant) {
        log.info("IN MerchantServiceImpl, create -{}", merchant);
        return merchantRepository.save(merchant.toBuilder()
                .createAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Merchant> update(Merchant merchant) {
        log.info("IN MerchantServiceImpl, update -{}", merchant);
        return merchantRepository
                .findById(merchant.getId())
                .map(x -> x.toBuilder()
                        .accountMerchants(merchant.getAccountMerchants())
                        .updateAt(LocalDateTime.now())
                        .build()).flatMap(merchantRepository::save);
    }


    @Override
    public Flux<Merchant> findAll() {
        log.info("IN MerchantServiceImpl, findAll");
        return Flux.from(merchantRepository.findAll());
    }

}
