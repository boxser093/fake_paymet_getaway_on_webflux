package com.ilya.payment_getaway.repository;

import com.ilya.payment_getaway.entity.Customer;
import com.ilya.payment_getaway.entity.CustomerCard;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface CustomerRepository extends R2dbcRepository<Customer, Long> {
    @Query("SELECT * FROM customers where first_name=:firstName and last_name=:lastName FOR UPDATE")
    Mono<Customer> findCustomerByFirstNameAndLastName(String firstName, String lastName);
}
