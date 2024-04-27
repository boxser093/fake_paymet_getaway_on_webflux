package com.ilya.payment_getaway.rest;

import com.ilya.payment_getaway.dto.AccountDto;

import com.ilya.payment_getaway.mapper.AccountMapper;
import com.ilya.payment_getaway.security.ApplicationConstants;
import com.ilya.payment_getaway.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Merchant balance controller", description = "Show balance operation")
@RequestMapping("/api/v1/merchants/balance")
public class BalanceRestControllerV1 {
    private final MerchantService merchantService;
    private final AccountMapper accountMapper;

    @SecurityRequirement(name = "Basic")
    @Operation(summary = "Get merchant balance",
            description = "We got a balance account merchant...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance merchant",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDto.class))}),
            @ApiResponse(responseCode = "403", description = "Merchant not found, access denied",
                    content = @Content)})
    @GetMapping
    public Mono<?> getBalanceForMerchants(ServerWebExchange exchange) {
        return merchantService.getMerchantBalance(exchange.getAttribute(ApplicationConstants.MERCHANT_ID)).map(accountMapper::map);
    }
}