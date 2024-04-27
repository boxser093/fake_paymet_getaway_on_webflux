package com.ilya.payment_getaway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("customer_cards")
public class CustomerCard {

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

    @Getter
    @Transient
    @ToString.Exclude
    private Customer customer;

}
