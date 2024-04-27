package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.Merchant;
import com.ilya.payment_getaway.repository.AccountMerchantRepository;
import com.ilya.payment_getaway.repository.MerchantRepository;
import com.ilya.payment_getaway.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantServiceImplTest {
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private AccountMerchantRepository accountMerchantRepository;
    @InjectMocks
    private MerchantServiceImpl merchantService;

    @Test
    void getMerchantBalance() {
        //given
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        Long id = merchantJimCarrey.getId();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        AccountMerchant accountMerchantRussellCrowe = DateUtils.getAccountMerchantRussellCrowe();
        Flux<AccountMerchant> just = Flux.just(accountMerchantJimCarrey, accountMerchantRussellCrowe);
        List<AccountMerchant> accountMerchantJimCarrey1 = List.of(accountMerchantJimCarrey);
        //when
        when(accountMerchantRepository.findAll()).thenReturn(just);
        //then
        StepVerifier
                .create(merchantService.getMerchantBalance(merchantJimCarrey.getId()))
                .expectNextMatches(accountMerchants -> accountMerchants.get(0).equals(accountMerchantJimCarrey1.get(0))
                        && accountMerchants.get(0).getCurrency().equals(accountMerchantJimCarrey.getCurrency())
                        && accountMerchants.get(0).getBalance().equals(accountMerchantJimCarrey.getBalance()))
                .expectComplete()
                .verify();
    }

    @Test
    void findById() {
        //given
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        Long id = merchantJimCarrey.getId();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        AccountMerchant accountMerchantRussellCrowe = DateUtils.getAccountMerchantRussellCrowe();
        Flux<AccountMerchant> just = Flux.just(accountMerchantJimCarrey, accountMerchantRussellCrowe);
        //when
        when(accountMerchantRepository.findAll()).thenReturn(just);
        when(merchantRepository.findById(id)).thenReturn(Mono.just(merchantJimCarrey));
        //then
        StepVerifier
                .create(merchantService.findById(1L))
                .expectNextMatches(merchant -> merchant.getId().equals(merchantJimCarrey.getId())
                        && merchant.getAccountMerchants().get(0).equals(accountMerchantJimCarrey))
                .expectComplete()
                .verify();

    }

    @Test
    void create() {
        Merchant before = Merchant.builder()
                .key("")
                .accountMerchants(List.of())
                .build();

        Merchant after = DateUtils.getMerchantJimCarrey();
        //when
        when(merchantRepository.save(any(Merchant.class)))
                .thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(merchantService.create(before))
                .expectNextMatches(merchant -> merchant.getId().equals(after.getId())
                        && merchant.getKey().equals(after.getKey()))
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        //given
        Merchant beforeUpdate = DateUtils.getMerchantJimCarrey().toBuilder()
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        Merchant afterUpdate = Merchant.builder()
                .id(beforeUpdate.getId())
                .merchantId("PEDRO")
                .key("12345543")
                .updateAt(LocalDateTime.now())
                .build();
        //when
        when(merchantRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(merchantRepository.save(any(Merchant.class))).thenReturn(Mono.just(afterUpdate));
        //then
        StepVerifier
                .create(merchantService.update(beforeUpdate))
                .expectNextMatches(merchant -> merchant.getId().equals(beforeUpdate.getId())
                        && merchant.getMerchantId().equals(afterUpdate.getMerchantId())
                        && merchant.getKey().equals(afterUpdate.getKey()))
                .expectComplete()
                .verify();
    }


    @Test
    void findAll() {
        //given
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        Merchant build = Merchant.builder()
                .id(2L)
                .merchantId("FAUST")
                .key("Some")
                .build();
        Flux<Merchant> just = Flux.just(merchantJimCarrey, build);
        //when
        when(merchantRepository.findAll()).thenReturn(just);
        //then
        StepVerifier
                .create(merchantService.findAll())
                .expectNext(merchantJimCarrey)
                .expectNext(build)
                .verifyComplete();
    }
}