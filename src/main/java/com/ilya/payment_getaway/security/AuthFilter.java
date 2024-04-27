package com.ilya.payment_getaway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthFilter implements WebFilter {

    private final SecurityService securityService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String first = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return securityService.checkMerchants(first)
                .flatMap(merchant -> {
                    if (merchant != null) {
                        exchange.getAttributes().put(ApplicationConstants.MERCHANT_ID, merchant.getId());
                        exchange.getAttributes().put(ApplicationConstants.MERCHANT_ENTITY, merchant);
                        return chain.filter(exchange);
                    } else {
                        return exchange.getResponse().setComplete();
                    }
                });
    }
}