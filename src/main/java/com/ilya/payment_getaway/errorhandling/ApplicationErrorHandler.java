package com.ilya.payment_getaway.errorhandling;


import com.ilya.payment_getaway.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationErrorHandler {

    @ExceptionHandler(NoAuthMerch.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(NoAuthMerch e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder()
                        .errorCode(403)
                        .message(String.format("Not auth for %s", e.getMerchantId()))
                        .build());
    }

    @ExceptionHandler(NoCustomer.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(NoCustomer e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .errorCode(404)
                        .message(String.format("No such customer %s" + "\n Send transaction again!", e.getCustomer()))
                        .build());
    }

    @ExceptionHandler(TransactionTopUtFailed.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(TransactionTopUtFailed e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message(String.format("Not enough money on balance %s" +
                                "\n Send transaction again!", e.getCustomerCard()))
                        .build());
    }

    @ExceptionHandler(TransactionWithdrawalFiled.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(TransactionWithdrawalFiled e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message(String.format("Not enough money on balance %s" +
                                "\n Send transaction again!", e.getAccountMerchant()))
                        .build());
    }

    @ExceptionHandler(NoTransaction.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(NoTransaction e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message(String.format("Patter of search no available  %s" +
                                "\n Tray again", e.getPattern()))
                        .build());
    }

    @ExceptionHandler(WebhookError.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(WebhookError e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(400)
                        .message(String.format("WEBHOOK ERROR!  %s" +
                                "\n CLIENT NOT NOTIFY", e.getMessage()))
                        .build());
    }

    @ExceptionHandler(RetryOver.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RetryOver e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.builder()
                        .errorCode(502)
                        .message(String.format("Retry over!  %s" +
                                "\n CLIENT NOT NOTIFY", e.getMessage()))
                        .build());
    }

}
