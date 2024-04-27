package com.ilya.payment_getaway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("merchants")
public class Merchant {

    @Id
    private Long id;
    private String merchantId;
    private String key;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @ToString.Exclude
    @Transient
    private List<AccountMerchant> accountMerchants;

    @ToString.Include(name = "key")
    private String mastKey() {
        return "********";
    }

}
