package com.ilya.payment_getaway.client;

import com.ilya.payment_getaway.errorhandling.WebhookError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebClientService {
    private final WebClient webClient;
    public Mono<String> fetchWebClient(String requestUrl, String bodyRequest) {
        return webClient.post()
                .uri(requestUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(bodyRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(() -> new WebhookError("Http Status code 400")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(() -> new WebhookError("Http Status code 500")))
                .bodyToMono(String.class);
    }
}
