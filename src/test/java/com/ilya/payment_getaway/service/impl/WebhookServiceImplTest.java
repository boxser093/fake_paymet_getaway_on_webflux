package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.client.WebClientService;
import com.ilya.payment_getaway.entity.TransactionType;
import com.ilya.payment_getaway.entity.Webhook;
import com.ilya.payment_getaway.errorhandling.RetryOver;
import com.ilya.payment_getaway.errorhandling.WebhookError;
import com.ilya.payment_getaway.repository.WebhookRepository;
import com.ilya.payment_getaway.security.ApplicationConstants;
import com.ilya.payment_getaway.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceImplTest {
    @Mock
    private WebClientService webClient;
    @Mock
    private WebhookRepository webhookRepository;
    @InjectMocks
    private WebhookServiceImpl webhookService;

    @Test
    void sendWebhook_Successful() {
        //given
        Webhook webhook = DateUtils.getWebhook();
        webhook.setBodyRequest("TESTED OK");
        String requestUrl = webhook.getRequestUrl();
        String body = "TESTED OK";
        //when
        when(webClient.fetchWebClient(eq(requestUrl), eq(body))).thenReturn(Mono.just(body));
        //then
        StepVerifier
                .create(webhookService.sendWebhook(webhook))
                .expectComplete()
                .verify();
        verify(webhookRepository, timeout(1)).save(webhook.toBuilder()
                .id(null)
                .bodyResponse(body)
                .responseStatus(ApplicationConstants.WEBHOOK_SUCCESSFUL)
                .tryNumber(1L)
                .createAt(any())
                .build());
        assertDoesNotThrow(() -> webhookService.sendWebhook(webhook));
    }

    @Test
    void sendWebhook_Unsuccessful() {
        //given
        Webhook webhook = DateUtils.getWebhook().toBuilder()
                .bodyRequest("TEST RETRY")
                .build();
        Webhook build = webhook.toBuilder()
                .id(null)
                .bodyResponse("Http Status code 400")
                .responseStatus(ApplicationConstants.WEBHOOK_UNSUCCESSFUL)
                .tryNumber(0L)
                .createAt(LocalDateTime.now())
                .build();

        String requestUrl = webhook.getRequestUrl();
        String body = "TEST RETRY";
        //when
        when(webClient.fetchWebClient(eq(requestUrl), eq(body)))
                .thenReturn(Mono.error(() -> new WebhookError("Http Status code 400")));
        when(webhookRepository.save(any(Webhook.class))).thenReturn(Mono.just(build));
        //then
        StepVerifier
                .create(webhookService.sendWebhook(webhook))
                .expectComplete()
                .verify();
        verify(webhookRepository, times(4)).save(any(Webhook.class));


    }

    @Test
    void findById() {
        //given
        Webhook build = Webhook.builder()
                .id(1L)
                .accountId(2L)
                .transactionId(UUID.randomUUID())
                .bodyRequest("Some Body")
                .requestUrl("http://foo.ru")
                .transactionType(TransactionType.WITHDRAWAL)
                .build();
        Long id = build.getId();
        //when
        when(webhookRepository.findById(id)).thenReturn(Mono.just(build));
        //then
        StepVerifier
                .create(webhookService.findById(1L))
                .expectNextMatches(webhook1 -> webhook1.getId().equals(webhook1.getId())
                        && webhook1.getRequestUrl().equals(build.getRequestUrl())
                        && webhook1.getTransactionId().equals(build.getTransactionId()))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        Webhook before = Webhook.builder()
                .accountId(2L)
                .transactionId(UUID.randomUUID())
                .bodyRequest("Some Body")
                .requestUrl("http://foo.ru")
                .transactionType(TransactionType.WITHDRAWAL)
                .build();
        Webhook after = DateUtils.getWebhook();
        //when
        when(webhookRepository.save(any(Webhook.class)))
                .thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(webhookService.create(before))
                .expectNextMatches(webhook -> webhook.getId().equals(after.getId())
                        && webhook.getRequestUrl().equals(after.getRequestUrl())
                        && webhook.getTransactionId().equals(after.getTransactionId()))
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        //given
        Webhook beforeUpdate = DateUtils.getWebhook().toBuilder()
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        Webhook afterUpdate = Webhook.builder()
                .id(2L)
                .responseStatus("200")
                .requestUrl(beforeUpdate.getRequestUrl())
                .requestUrl(beforeUpdate.getRequestUrl())
                .bodyResponse(beforeUpdate.getBodyResponse())
                .tryNumber(1L)
                .bodyRequest(beforeUpdate.getBodyRequest())
                .transactionType(beforeUpdate.getTransactionType())
                .transactionId(beforeUpdate.getTransactionId())
                .updateAt(LocalDateTime.now())
                .build();
        //when
        when(webhookRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(webhookRepository.save(any(Webhook.class))).thenReturn(Mono.just(afterUpdate));
        //then
        StepVerifier
                .create(webhookService.update(beforeUpdate))
                .expectNextMatches(webhook -> webhook.getId().equals(afterUpdate.getId())
                        && webhook.getTryNumber().equals(afterUpdate.getTryNumber())
                        && webhook.getResponseStatus().equals(afterUpdate.getResponseStatus())
                        && webhook.getBodyRequest().equals(beforeUpdate.getBodyRequest())
                        && webhook.getRequestUrl().equals(beforeUpdate.getRequestUrl()))
                .expectComplete()
                .verify();
    }

    @Test
    void findAll() {
        //given
        Webhook webhook = DateUtils.getWebhook().toBuilder()
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        Webhook webhook1 = Webhook.builder()
                .id(2L)
                .responseStatus("200")
                .requestUrl(webhook.getRequestUrl())
                .requestUrl(webhook.getRequestUrl())
                .bodyResponse(webhook.getBodyResponse())
                .tryNumber(1L)
                .bodyRequest(webhook.getBodyRequest())
                .transactionType(webhook.getTransactionType())
                .transactionId(webhook.getTransactionId())
                .updateAt(LocalDateTime.now())
                .build();
        Flux<Webhook> just = Flux.just(webhook, webhook1);
        //when
        when(webhookRepository.findAll()).thenReturn(just);
        //then
        StepVerifier
                .create(webhookService.findAll())
                .expectNext(webhook)
                .expectNext(webhook1)
                .verifyComplete();
    }

    @Test
    void givenWebhook_whenSaveSuccessfulWebhook_thenSuccessfulResult() {
        String response = "Hello from successful!";

        Webhook before = Webhook.builder()
                .id(1L)
                .accountId(2L)
                .transactionId(UUID.randomUUID())
                .bodyRequest("Some Body")
                .requestUrl("http://foo.ru")
                .transactionType(TransactionType.WITHDRAWAL)
                .tryNumber(1L)
                .bodyResponse("")
                .createAt(LocalDateTime.now())
                .build();
        Webhook after = Webhook.builder()
                .id(2L)
                .accountId(2L)
                .transactionId(UUID.randomUUID())
                .bodyRequest("Hello from successful!")
                .requestUrl("http://foo.ru")
                .transactionType(TransactionType.WITHDRAWAL)
                .responseStatus(ApplicationConstants.WEBHOOK_SUCCESSFUL)
                .createAt(LocalDateTime.now())
                .build();
        //when
        when(webhookRepository.save(any(Webhook.class)))
                .thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(webhookService.saveSuccessfulWebhook(before, response))
                .expectNextMatches(webhook -> webhook.getId().equals(after.getId())
                        && webhook.getBodyRequest().equals(response)
                        && webhook.getResponseStatus().equals(ApplicationConstants.WEBHOOK_SUCCESSFUL))
                .expectComplete()
                .verify();


    }

    @Test
    void handleRetryWebhookUnsuccessful() {
        RetryOver retryOver = RetryOver.builder()
                .row(5L)
                .message("Hello from tests")
                .build();

        Webhook before = Webhook.builder()
                .id(1L)
                .accountId(2L)
                .transactionId(UUID.randomUUID())
                .bodyRequest("Some Body")
                .requestUrl("http://foo.ru")
                .transactionType(TransactionType.TOP_UP)
                .tryNumber(5L)
                .bodyResponse("")
                .createAt(LocalDateTime.now())
                .build();

        Webhook after = Webhook.builder()
                .id(3L)
                .accountId(3L)
                .transactionId(UUID.randomUUID())
                .bodyRequest("Some Body")
                .requestUrl("http://foos.ru")
                .transactionType(TransactionType.TOP_UP)
                .responseStatus(ApplicationConstants.WEBHOOK_UNSUCCESSFUL)
                .tryNumber(retryOver.getRow())
                .bodyResponse(retryOver.getMessage())
                .createAt(LocalDateTime.now())
                .build();
        //when
        when(webhookRepository.save(any(Webhook.class)))
                .thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(webhookService.handleRetryExhaustedWebhookUnsuccessful(before, retryOver))
                .expectNextMatches(webhook -> webhook.getId().equals(after.getId())
                        && webhook.getTryNumber().equals(retryOver.getRow())
                        && webhook.getBodyResponse().equals(retryOver.getMessage())
                        && webhook.getResponseStatus().equals(ApplicationConstants.WEBHOOK_UNSUCCESSFUL))
                .expectComplete()
                .verify();
    }

    @Test
    void handleRetryExhaustedWebhookUnsuccessful() {
        RetryOver retryOver = RetryOver.builder()
                .row(999L)
                .message("Hello from tests and this last Retry")
                .build();

        Webhook before = Webhook.builder()
                .id(1L)
                .accountId(2L)
                .transactionId(UUID.randomUUID())
                .bodyRequest("Some Body")
                .requestUrl("http://foo.ru")
                .transactionType(TransactionType.TOP_UP)
                .tryNumber(999L)
                .bodyResponse("")
                .createAt(LocalDateTime.now())
                .build();

        Webhook after = Webhook.builder()
                .id(3L)
                .accountId(3L)
                .transactionId(UUID.randomUUID())
                .bodyRequest("Some Body")
                .requestUrl("http://foos.ru")
                .transactionType(TransactionType.TOP_UP)
                .responseStatus(ApplicationConstants.WEBHOOK_UNSUCCESSFUL)
                .tryNumber(retryOver.getRow())
                .bodyResponse(retryOver.getMessage())
                .createAt(LocalDateTime.now())
                .build();
        //when
        when(webhookRepository.save(any(Webhook.class)))
                .thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(webhookService.handleRetryExhaustedWebhookUnsuccessful(before, retryOver))
                .expectNextMatches(webhook -> webhook.getId().equals(after.getId())
                        && webhook.getTryNumber().equals(retryOver.getRow())
                        && webhook.getBodyResponse().equals(retryOver.getMessage())
                        && webhook.getResponseStatus().equals(ApplicationConstants.WEBHOOK_UNSUCCESSFUL))
                .expectComplete()
                .verify();
    }
}