package com.ilya.payment_getaway.service;

import com.ilya.payment_getaway.entity.CustomerCard;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

public interface CustomerCardService extends GenericService<CustomerCard, Long>{
    @Query("SELECT * FROM customer_cards where cart_number=:cartNumber FOR UPDATE")
    Mono<CustomerCard> findCustomerCardByCartNumber(String cartNumber);
}
