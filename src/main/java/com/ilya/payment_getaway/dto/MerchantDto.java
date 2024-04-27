package com.ilya.payment_getaway.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Merchants DTO")
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MerchantDto {

    @Id
    private Long id;
    private String merchantId;
    private String key;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<AccountDto> accountMerchants;
}
