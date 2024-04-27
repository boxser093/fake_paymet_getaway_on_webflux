package com.ilya.payment_getaway.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("webhooks")
public class Webhook {
    @Id
    private Long id;
    private UUID transactionId;
    private Long accountId;
    private String bodyRequest;
    private String requestUrl;
    private String responseStatus;
    private String bodyResponse;
    private TransactionType transactionType;
    private Long tryNumber;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
