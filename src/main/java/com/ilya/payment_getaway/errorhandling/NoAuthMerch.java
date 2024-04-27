package com.ilya.payment_getaway.errorhandling;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class NoAuthMerch extends SecurityException {

    private final String merchantId;
    private static final String MESSAGE = "Merchant not found";

}
