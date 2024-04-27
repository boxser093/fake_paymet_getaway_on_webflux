package com.ilya.payment_getaway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("transactions")
public class Transaction {
    @Id
    private UUID id;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private Currency currency;
    private Long customerCardId;
    private Long accountId;
    private String notificationUrl;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @Transient
    private Customer customer;

    @Transient
    private CustomerCard customerCard;
}
