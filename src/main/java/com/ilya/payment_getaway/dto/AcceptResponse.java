package com.ilya.payment_getaway.dto;

import com.ilya.payment_getaway.entity.Transaction;
import com.ilya.payment_getaway.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AcceptResponse {

    private UUID transactionCode;
    private TransactionStatus status;
    private final String OPERATION_STATUS = "OK";
}
