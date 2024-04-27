package com.ilya.payment_getaway.errorhandling;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class TransactionWithdrawalFiled extends ArithmeticException{

    private final String accountMerchant;
    private static final String MESSAGE = "Not enough money on account!";

}
