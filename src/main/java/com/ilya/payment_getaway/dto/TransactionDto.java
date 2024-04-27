package com.ilya.payment_getaway.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ilya.payment_getaway.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Transaction Dto")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionDto {

    @Id
    private UUID id;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private Currency currency;
    private CustomerCardDto customerCard;
    private String notificationUrl;
    private CustomerDto customer;
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
    private Long customerCardId;
    private Long accountId;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
