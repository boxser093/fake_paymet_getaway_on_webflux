package com.ilya.payment_getaway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("customers")
public class Customer {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String country;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @Transient
    @ToString.Exclude
    private List<CustomerCard> customerCardList;

}
