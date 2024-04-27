package com.ilya.payment_getaway.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ilya.payment_getaway.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Webhooks")
public class WebhookDto {

    @Id
    private Long id;
    private Long accountId;
    private UUID transactionId;
    private String bodyRequest;
    private String requestUrl;
    private String responseStatus;
    private String bodyResponse;
    private TransactionType transactionType;
    private Long tryNumber;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
