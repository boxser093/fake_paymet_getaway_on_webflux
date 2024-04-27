package com.ilya.payment_getaway.repository;

import com.ilya.payment_getaway.entity.Webhook;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookRepository extends R2dbcRepository<Webhook, Long> {
}
