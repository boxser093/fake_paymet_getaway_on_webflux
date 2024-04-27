package com.ilya.payment_getaway.errorhandling;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class NoTransaction extends ChangeSetPersister.NotFoundException {
    private final String pattern;
    private static final String MESSAGE = "There are no transactions suitable for the conditions!";
}
