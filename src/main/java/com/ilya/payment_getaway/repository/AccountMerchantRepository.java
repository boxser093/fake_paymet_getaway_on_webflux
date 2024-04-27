package com.ilya.payment_getaway.repository;

import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.Currency;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountMerchantRepository extends R2dbcRepository<AccountMerchant, Long> {
    @Query("SELECT * FROM accounts where merchant_id=:id and currency=:currencyType FOR UPDATE ")
    Mono<AccountMerchant> findAccountMerchantByMerchantIdAndCurrency(Long id, Currency currencyType);
    @Query("SELECT * FROM accounts where merchant_id=:merchantId FOR UPDATE ")
    Flux<AccountMerchant> findAllByMerchantId (Long merchantId);
}
