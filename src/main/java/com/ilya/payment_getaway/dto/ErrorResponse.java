package com.ilya.payment_getaway.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ErrorResponse {

    private int errorCode;
    private String message;
}
