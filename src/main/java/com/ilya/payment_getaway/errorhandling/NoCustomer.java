package com.ilya.payment_getaway.errorhandling;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class NoCustomer extends NoSuchElementException {

    private final String customer;
    private static final String MESSAGE = "Customer not found";
}
