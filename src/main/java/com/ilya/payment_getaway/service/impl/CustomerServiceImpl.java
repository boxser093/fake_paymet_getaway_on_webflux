package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.Customer;
import com.ilya.payment_getaway.entity.CustomerCard;
import com.ilya.payment_getaway.repository.CustomerCardRepository;
import com.ilya.payment_getaway.repository.CustomerRepository;
import com.ilya.payment_getaway.service.CustomerService;
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
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerCardRepository customerCardRepository;

    @Override
    public Mono<Customer> findCustomerByFirstNameAndLastName(String firstName, String lastName) {
        log.info("IN CustomerServiceImpl, findCustomerByFirstNameAndLastName -{}, {}", firstName, lastName);
        return customerRepository.findCustomerByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public Mono<Customer> findById(Long aLong) {
        log.info("IN CustomerServiceImpl, findById -{}", aLong);
        Mono<Customer> byId = customerRepository.findById(aLong);
        Mono<List<CustomerCard>> listMono = customerCardRepository.findAll().filter(x -> x.getCustomerId().equals(aLong)).collectList();
        return Mono.zip(byId, listMono).map(t -> t.getT1().toBuilder()
                    .customerCardList(t.getT2())
                    .build());
    }

    @Override
    public Mono<Customer> create(Customer customer) {
        log.info("IN CustomerServiceImpl, create -{}", customer);
        return customerRepository.save(customer.toBuilder()
                .createAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Customer> update(Customer customer) {
        log.info("IN CustomerServiceImpl, update -{}", customer);
        return customerRepository.findById(customer.getId())
                .map(fromBd -> fromBd.toBuilder()
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .country(customer.getCountry())
                        .customerCardList(customer.getCustomerCardList())
                        .updateAt(LocalDateTime.now())
                        .build())
                .flatMap(customerRepository::save);
    }


    @Override
    public Flux<Customer> findAll() {
        log.info("IN CustomerServiceImpl, findAll");
        return customerRepository.findAll();
    }
}
