package com.ilya.payment_getaway.errorhandling;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class NoCustomerCard extends NoSuchElementException {

    private final String customerCard;
    private static final String MESSAGE = "Customer not found";

}
