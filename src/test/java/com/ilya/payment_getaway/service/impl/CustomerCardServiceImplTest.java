package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.Currency;
import com.ilya.payment_getaway.entity.CustomerCard;
import com.ilya.payment_getaway.repository.CustomerCardRepository;
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
class CustomerCardServiceImplTest {
    @Mock
    private CustomerCardRepository customerCardRepository;
    @InjectMocks
    public CustomerCardServiceImpl customerCardService;

    @Test
    void givenFindCustomerCardByCartNumber_whenCustomerCardRegistered_thenSuccessfulness() {
        //given
        CustomerCard givenCard = DateUtils.getCustomerCardGutsBerserk();
        String cartNumber = givenCard.getCartNumber();
        //when
        when(customerCardRepository.findCustomerCardByCartNumber(cartNumber)).thenReturn(Mono.just(givenCard));
        //then
        StepVerifier
                .create(customerCardService.findCustomerCardByCartNumber("4102778822334896"))
                .expectNextMatches(card -> card.getId().equals(givenCard.getId())
                        && card.getCurrency().equals(givenCard.getCurrency())
                        && card.getCvv().equals(givenCard.getCvv()))
                .expectComplete()
                .verify();

    }

    @Test
    void givenFindById_whenCustomerCardRegistered_thenSuccessfulness() {
        CustomerCard givenCard = DateUtils.getCustomerCardGutsBerserk();
        Long id = givenCard.getId();
        //when
        when(customerCardRepository.findById(id)).thenReturn(Mono.just(givenCard));
        //then
        StepVerifier
                .create(customerCardService.findById(1L))
                .expectNextMatches(card -> card.getId().equals(givenCard.getId())
                        && card.getCurrency().equals(givenCard.getCurrency())
                        && card.getCvv().equals(givenCard.getCvv()))
                .expectComplete()
                .verify();

    }

    @Test
    void givenCustomerCard_whenCreateCustomerCard_thenSuccessfulness() {
        //given

        CustomerCard before = CustomerCard.builder()
                .cartNumber("4102778822334896")
                .expDate("11/24")
                .cvv("543")
                .balance(BigDecimal.valueOf(5000))
                .currency(Currency.USD)
                .customerId(1L)
                .build();
        CustomerCard after = DateUtils.getCustomerCardGutsBerserk();
        //when
        when(customerCardRepository.save(any(CustomerCard.class)))
                .thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(customerCardService.create(before))
                .expectNextMatches(card -> card.getId().equals(after.getId())
                        && card.getCartNumber().equals(after.getCartNumber())
                        && card.getBalance().equals(after.getBalance()))
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        //given
        CustomerCard beforeUpdate = DateUtils.getCustomerCardGutsBerserk().toBuilder()
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        CustomerCard afterUpdate = CustomerCard.builder()
                .id(beforeUpdate.getId())
                .cartNumber("132451345")
                .balance(BigDecimal.valueOf(666))
                .updateAt(LocalDateTime.now())
                .build();
        //when
        when(customerCardRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(customerCardRepository.save(any(CustomerCard.class))).thenReturn(Mono.just(afterUpdate));
        //then
        StepVerifier
                .create(customerCardService.update(beforeUpdate))
                .expectNextMatches(card -> card.getId().equals(beforeUpdate.getId())
                        && card.getCartNumber().equals(afterUpdate.getCartNumber())
                        && card.getBalance().equals(afterUpdate.getBalance()))
                .expectComplete()
                .verify();
    }

    @Test
    void findAll() {
        //given
        CustomerCard customerCardGutsBerserk = DateUtils.getCustomerCardGutsBerserk();
        CustomerCard customerCardJohnCena = DateUtils.getCustomerCardJohnCena();
        Flux<CustomerCard> just = Flux.just(customerCardGutsBerserk, customerCardJohnCena);
        //when
        when(customerCardRepository.findAll()).thenReturn(just);
        //then
        StepVerifier
                .create(customerCardService.findAll())
                .expectNext(customerCardGutsBerserk)
                .expectNext(customerCardJohnCena)
                .verifyComplete();
    }
}