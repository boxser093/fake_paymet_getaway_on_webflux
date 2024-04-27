package com.ilya.payment_getaway.errorhandling;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class TransactionTopUtFailed extends ArithmeticException {

    private final String customerCard;
    private static final String MESSAGE = "Not enough money on customer card!";

}
