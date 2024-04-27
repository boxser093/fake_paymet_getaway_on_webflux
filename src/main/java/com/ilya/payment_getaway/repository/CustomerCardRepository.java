package com.ilya.payment_getaway.repository;

import com.ilya.payment_getaway.entity.CustomerCard;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface CustomerCardRepository extends R2dbcRepository<CustomerCard, Long> {
    @Query("SELECT * FROM customer_cards where cart_number=:cartNumber FOR UPDATE")
    Mono<CustomerCard> findCustomerCardByCartNumber(String cartNumber);
}
