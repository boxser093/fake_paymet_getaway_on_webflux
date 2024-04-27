package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.Currency;
import com.ilya.payment_getaway.entity.Merchant;
import com.ilya.payment_getaway.repository.AccountMerchantRepository;
import com.ilya.payment_getaway.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountMerchantRepository accountMerchantRepository;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void findById() {
        //given
        AccountMerchant accountMerchantRussellCrowe = DateUtils.getAccountMerchantRussellCrowe();
        //when
        when(accountMerchantRepository.findById(2L)).thenReturn(Mono.just(accountMerchantRussellCrowe));
        //then
        StepVerifier
                .create(accountService.findById(2L))
                .expectNextMatches(accountMerchant -> accountMerchant.getId().equals(accountMerchantRussellCrowe.getId())
                        && accountMerchant.getBalance().equals((accountMerchant.getBalance()))
                        && accountMerchant.getMerchantId().equals(accountMerchantRussellCrowe.getMerchantId()))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        //given
        AccountMerchant after = DateUtils.getAccountMerchantJimCarrey().toBuilder()
                .createAt(LocalDateTime.now())
                .build();

        AccountMerchant before = AccountMerchant.builder()
                .merchantId(1L)
                .balance(BigDecimal.valueOf(125000))
                .currency(Currency.USD)
                .build();
        //when
        when(accountMerchantRepository.save(any(AccountMerchant.class))).thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(accountService.create(before))
                .expectNextMatches(accountMerchant -> accountMerchant.getId().equals(after.getId())
                        && accountMerchant.getBalance().equals((after.getBalance()))
                        && accountMerchant.getMerchantId().equals(after.getMerchantId()))
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        //given
        AccountMerchant beforeUpdate = DateUtils.getAccountMerchantJimCarrey().toBuilder()
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        AccountMerchant afterUpdate = AccountMerchant.builder()
                .id(beforeUpdate.getId())
                .merchantId(146L)
                .merchant(merchantJimCarrey)
                .updateAt(LocalDateTime.now())
                .build();
        //when
        when(accountMerchantRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(accountMerchantRepository.save(any(AccountMerchant.class))).thenReturn(Mono.just(afterUpdate));
        //then
        StepVerifier
                .create(accountService.update(beforeUpdate))
                .expectNextMatches(accountMerchant -> accountMerchant.getId().equals(beforeUpdate.getId())
                        && accountMerchant.getMerchantId().equals(afterUpdate.getMerchantId())
                        && accountMerchant.getMerchant().equals(afterUpdate.getMerchant()))
                .expectComplete()
                .verify();
    }

    @Test
    void findAll() {
        //given
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        AccountMerchant accountMerchantRussellCrowe = DateUtils.getAccountMerchantRussellCrowe();
        Flux<AccountMerchant> justAll = Flux.just(accountMerchantRussellCrowe, accountMerchantJimCarrey);
        //when
        when(accountMerchantRepository.findAll()).thenReturn(justAll);
        //then
        StepVerifier
                .create(accountService.findAll())
                .expectNext(accountMerchantRussellCrowe)
                .expectNext(accountMerchantJimCarrey)
                .verifyComplete();
    }

    @Test
    void findByMerchantIdAndCurrency() {
        //given
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        Long id = accountMerchantJimCarrey.getId();
        Currency currency = accountMerchantJimCarrey.getCurrency();
        //when
        when(accountMerchantRepository.findAccountMerchantByMerchantIdAndCurrency(id,currency))
                .thenReturn(Mono.just(accountMerchantJimCarrey));
        //then
        StepVerifier
                .create(accountService.findByMerchantIdAndCurrency(accountMerchantJimCarrey.getId(),accountMerchantJimCarrey.getCurrency()))
                .expectNextMatches(accountMerchant -> accountMerchant.getId().equals(accountMerchantJimCarrey.getId())
                        && accountMerchant.getCurrency().equals(accountMerchantJimCarrey.getCurrency()))
                .expectComplete()
                .verify();
    }

    @Test
    void findAllByMerchantId() {
        //given
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey().toBuilder()
                .merchantId(1L)
                .build();
        AccountMerchant accountMerchantRussellCrowe = DateUtils.getAccountMerchantRussellCrowe().toBuilder()
                .merchantId(1L)
                .build();
        Flux<AccountMerchant> justAll = Flux.just(accountMerchantRussellCrowe, accountMerchantJimCarrey);
        //when
        when(accountMerchantRepository.findAllByMerchantId(1L)).thenReturn(justAll);
        //then
        StepVerifier
                .create(accountService.findAllByMerchantId(1L))
                .expectNext(accountMerchantRussellCrowe)
                .expectNext(accountMerchantJimCarrey)
                .verifyComplete();
    }
}