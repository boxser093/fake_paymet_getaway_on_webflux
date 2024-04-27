package com.ilya.payment_getaway.repository;

import com.ilya.payment_getaway.entity.Merchant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface MerchantRepository extends R2dbcRepository<Merchant, Long> {
}
