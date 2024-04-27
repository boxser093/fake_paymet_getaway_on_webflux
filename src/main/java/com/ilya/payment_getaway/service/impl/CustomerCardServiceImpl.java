package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.CustomerCard;
import com.ilya.payment_getaway.repository.CustomerCardRepository;
import com.ilya.payment_getaway.service.CustomerCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerCardServiceImpl implements CustomerCardService {
    private final CustomerCardRepository customerCardRepository;

    @Override
    public Mono<CustomerCard> findCustomerCardByCartNumber(String number) {
        log.info("IN CustomerCardServiceImpl, findCustomerCardByCartNumber : {}", number);
        return customerCardRepository.findCustomerCardByCartNumber(number);
    }

    @Override
    public Mono<CustomerCard> findById(Long aLong) {
        log.info("IN CustomerCardServiceImpl, findById -{}", aLong);
        return customerCardRepository.findById(aLong);
    }

    @Override
    public Mono<CustomerCard> create(CustomerCard customerCard) {
        log.info("IN CustomerCardServiceImpl, create : {}", customerCard);
        return customerCardRepository.save(customerCard.toBuilder()
                .balance(BigDecimal.ZERO)
                .createAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<CustomerCard> update(CustomerCard customerCard) {
        log.info("IN CustomerCardServiceImpl, update -{}", customerCard);
        return customerCardRepository.findById(customerCard.getId())
                .map(cardFromDb -> cardFromDb.toBuilder()
                        .cartNumber(customerCard.getCartNumber())
                        .cvv(customerCard.getCvv())
                        .expDate(customerCard.getExpDate())
                        .balance(customerCard.getBalance())
                        .currency(customerCard.getCurrency())
                        .customerId(customerCard.getCustomerId())
                        .updateAt(LocalDateTime.now())
                        .build()).flatMap(customerCardRepository::save);
    }


    @Override
    public Flux<CustomerCard> findAll() {
        log.info("IN CustomerCardServiceImpl, findAll");
        return customerCardRepository.findAll();
    }
}
