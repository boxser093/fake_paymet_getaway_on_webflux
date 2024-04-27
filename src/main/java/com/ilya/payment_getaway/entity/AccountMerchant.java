package com.ilya.payment_getaway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("accounts")
public class AccountMerchant {
    @Id
    private Long id;
    private Long merchantId;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @ToString.Exclude
    @Transient
    private Merchant merchant;
}
