package com.ilya.payment_getaway.errorhandling;

import lombok.*;
import org.flywaydb.core.internal.strategy.RetryStrategy;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class RetryOver extends RuntimeException{
    private Long row;
    private String message;
}


