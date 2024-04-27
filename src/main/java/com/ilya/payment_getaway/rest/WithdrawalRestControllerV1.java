package com.ilya.payment_getaway.rest;

import com.ilya.payment_getaway.dto.AcceptResponse;
import com.ilya.payment_getaway.dto.TransactionDto;
import com.ilya.payment_getaway.entity.TransactionType;
import com.ilya.payment_getaway.mapper.TransactionMapper;
import com.ilya.payment_getaway.security.ApplicationConstants;
import com.ilya.payment_getaway.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Withdrawal controller", description = "Operation for payout")
@RequestMapping("/api/v1/payments/payout")
public class WithdrawalRestControllerV1 {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @SecurityRequirement(name = "Basic")
    @Operation(summary = "Get transaction by UUID",
            description = "We got a get transaction...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the transaction",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDto.class))}),
            @ApiResponse(responseCode = "403", description = "Merchant not found, access denied",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Transaction not found",
                    content = @Content)})
    @GetMapping("/{id}/details")
    public Mono<?> getPayoutTransactionById(ServerWebExchange webExchange, @PathVariable @Parameter(description = "Идентификатор транзакции",
            in = ParameterIn.PATH, name = "id", required = true, schema = @Schema(
            defaultValue = "1",
            minimum = "1",
            allOf = {String.class}),
            style = ParameterStyle.SIMPLE) String id) {
        return transactionService.findTransactionByIdAndMerchantId(webExchange.getAttribute(ApplicationConstants.MERCHANT_ID), UUID.fromString(id), TransactionType.WITHDRAWAL);
    }

    @SecurityRequirement(name = "Basic")
    @Operation(summary = "Create payout transaction",
            description = "We got a send transaction...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction payout created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AcceptResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Merchant not found, access denied",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Transaction failed",
                    content = @Content)})
    @PostMapping("/")
    public Mono<?> createTransactionTopUp(ServerWebExchange webExchange, @RequestBody
                                     @Parameter(description = "Transaction for payout balance",
                                             schema = @Schema(additionalPropertiesSchema = TransactionDto.class)) TransactionDto transactionDto) {
        return transactionService.payout(transactionMapper.map(transactionDto), webExchange.getAttribute(ApplicationConstants.MERCHANT_ENTITY));
    }

    @SecurityRequirement(name = "Basic")
    @Operation(summary = "Get all transaction by date or not",
            description = "We got a send transaction...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all transaction",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDto.class))}),
            @ApiResponse(responseCode = "403", description = "Merchant not found, access denied",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Not found transaction for u date",
                    content = @Content)})
    @GetMapping("/lists")
    public Flux<?> getAllPayoutTransactions( ServerWebExchange webExchange,
                                            @RequestParam(value = "start_date", required = false)
                             @Parameter(description = "start date for find transaction",
                                     in = ParameterIn.PATH, name = "start_date") String dateStart,
                                            @RequestParam(value = "end_date", required = false)
                             @Parameter(description = "end date for find transaction",
                                     in = ParameterIn.PATH, name = "end_date") String exitDate) {
        return transactionService.findAllByDateAndAccountId(webExchange.getAttribute(ApplicationConstants.MERCHANT_ENTITY), dateStart, exitDate, TransactionType.WITHDRAWAL);
    }

}
