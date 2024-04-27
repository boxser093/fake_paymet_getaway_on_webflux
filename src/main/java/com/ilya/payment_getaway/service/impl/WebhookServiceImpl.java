package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.client.WebClientService;
import com.ilya.payment_getaway.entity.Transaction;
import com.ilya.payment_getaway.entity.Webhook;
import com.ilya.payment_getaway.errorhandling.RetryOver;
import com.ilya.payment_getaway.errorhandling.WebhookError;
import com.ilya.payment_getaway.repository.WebhookRepository;
import com.ilya.payment_getaway.security.ApplicationConstants;
import com.ilya.payment_getaway.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {
    private final WebhookRepository webhookRepository;
    private final WebClientService webClient;

    @Override
    public Mono<Webhook> findById(Long aLong) {
        log.info("IN WebhookServiceImpl, findById -{}", aLong);
        return webhookRepository.findById(aLong);
    }

    @Override
    public Mono<Webhook> create(Webhook webhook) {
        log.info("IN WebhookServiceImpl, create -{}", webhook);
        return webhookRepository.save(webhook);
    }

    @Override
    public Mono<Webhook> update(Webhook webhook) {
        log.info("IN WebhookServiceImpl, update -{}", webhook);
        return webhookRepository.findById(webhook.getId())
                .map(webhook1 -> webhook1.toBuilder()
                        .updateAt(LocalDateTime.now())
                        .accountId(webhook.getAccountId())
                        .transactionId(webhook.getTransactionId())
                        .bodyRequest(webhook.getBodyRequest())
                        .bodyResponse(webhook.getBodyResponse())
                        .requestUrl(webhook.getRequestUrl())
                        .responseStatus(webhook.getResponseStatus())
                        .transactionType(webhook.getTransactionType())
                        .tryNumber(webhook.getTryNumber())
                        .build())
                .flatMap(webhookRepository::save);
    }

    @Override
    public Mono<Webhook> toWebhook(Transaction transaction) {
        return webhookRepository.save(Webhook.builder()
                .transactionId(transaction.getId())
                .accountId(transaction.getAccountId())
                .bodyRequest(transaction.toString())
                .requestUrl(transaction.getNotificationUrl())
                .responseStatus(null)
                .bodyResponse(null)
                .transactionType(transaction.getTransactionType())
                .tryNumber(0L)
                .createAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Void> sendWebhook(Webhook webhook) {
        log.info("IN WebhookServiceImpl sendWebhook BEGIN - {}", webhook);
        return webClient.fetchWebClient(webhook.getRequestUrl(), webhook.getBodyRequest())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .doBeforeRetryAsync(retrySignal -> handleRetryWebhookUnsuccessful(webhook, retrySignal).then())
                        .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                new RetryOver(retrySignal.totalRetries(), retrySignal.failure().getMessage())))
                        .scheduler(Schedulers.boundedElastic()))
                .flatMap(string -> saveSuccessfulWebhook(webhook, string).then())
                .onErrorResume(e -> {
                    if (e instanceof WebhookError exception) {
                        return Mono.just("Error handler webhook + " + exception.getMessage()).then();
                    } else if (e instanceof RetryOver) {
                        handleRetryExhaustedWebhookUnsuccessful(webhook, (RetryOver) e).subscribe();
                        return Mono.just("### IN sendWebhook Retry 3/3 is over! ###" + e.getMessage()).then();
                    } else return Mono.just("### IN sendWebhook got - an unexpected error! ###").then();
                });
    }

    @Override
    public Mono<Webhook> saveSuccessfulWebhook(Webhook webhook, String response) {
        log.info("Send webhook successful, response - {}", response);
        return webhookRepository.save(webhook.toBuilder()
                .id(null)
                .bodyResponse(response)
                .responseStatus(ApplicationConstants.WEBHOOK_SUCCESSFUL)
                .tryNumber(1L)
                .createAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Webhook> handleRetryWebhookUnsuccessful(Webhook webhook, Retry.RetrySignal signal) {
        log.error("Next Retry for  {}", signal);
        return webhookRepository.save(webhook.toBuilder()
                .id(null)
                .bodyResponse(signal.failure().getMessage())
                .responseStatus(ApplicationConstants.WEBHOOK_UNSUCCESSFUL)
                .tryNumber(signal.totalRetries())
                .createAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Mono<Webhook> handleRetryExhaustedWebhookUnsuccessful(Webhook webhook, RetryOver retryOver) {
        log.error("### IN sendWebhook Next Retry for {} ###", retryOver.toString());
        return webhookRepository.save(Webhook.builder()
                .transactionId(webhook.getTransactionId())
                .accountId(webhook.getAccountId())
                .bodyRequest(webhook.getBodyRequest())
                .requestUrl(webhook.getRequestUrl())
                .responseStatus(ApplicationConstants.WEBHOOK_UNSUCCESSFUL)
                .tryNumber(retryOver.getRow())
                .bodyResponse(retryOver.getMessage())
                .transactionType(webhook.getTransactionType())
                .createAt(LocalDateTime.now())
                .build());
    }

    @Override
    public Flux<Webhook> findAll() {
        log.info("IN WebhookServiceImpl, findAll ");
        return webhookRepository.findAll();
    }

}
