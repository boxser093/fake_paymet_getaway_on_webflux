package com.ilya.payment_getaway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ilya.payment_getaway.entity.Currency;
import com.ilya.payment_getaway.entity.Merchant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Account DTO")
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccountDto {
    @Id
    private Long id;
    private Long merchantId;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @JsonIgnore
    private Merchant merchant;
}
