package com.ilya.payment_getaway.repository;

import com.ilya.payment_getaway.entity.Transaction;
import com.ilya.payment_getaway.entity.TransactionStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {
    Flux<Transaction> findAllByTransactionStatus(TransactionStatus transactionStatus);
    @Query("SELECT * FROM transactions where account_id=:id FOR UPDATE")
    Flux<Transaction> findAllByAccountId(Long id);

}
