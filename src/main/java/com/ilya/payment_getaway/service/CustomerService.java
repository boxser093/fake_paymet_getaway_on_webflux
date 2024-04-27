package com.ilya.payment_getaway.service;

import com.ilya.payment_getaway.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerService extends GenericService<Customer, Long>{
    Mono<Customer> findCustomerByFirstNameAndLastName(String firstName, String lastName);
}
