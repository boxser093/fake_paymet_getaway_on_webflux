package com.ilya.payment_getaway.service;

import com.ilya.payment_getaway.entity.Transaction;
import com.ilya.payment_getaway.entity.Webhook;
import com.ilya.payment_getaway.errorhandling.RetryOver;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public interface WebhookService extends GenericService<Webhook, Long>{
    Mono<Void> sendWebhook(Webhook webhook);
    Mono<Webhook> toWebhook(Transaction transaction);
    Mono<Webhook> saveSuccessfulWebhook(Webhook webhook, String response);
    Mono<Webhook> handleRetryWebhookUnsuccessful(Webhook webhook, Retry.RetrySignal signal);
    Mono<Webhook> handleRetryExhaustedWebhookUnsuccessful(Webhook webhook, RetryOver retryOver);
}
