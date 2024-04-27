package com.ilya.payment_getaway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ilya.payment_getaway.entity.Currency;
import com.ilya.payment_getaway.entity.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Transaction Dto")
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerCardDto {
    @Id
    private Long id;
    private String cartNumber;
    private String expDate;
    private String cvv;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Long customerId;
    @JsonIgnore
    private Customer customer;
}
