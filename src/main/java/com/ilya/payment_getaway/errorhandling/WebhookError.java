package com.ilya.payment_getaway.errorhandling;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.rmi.ServerException;

@EqualsAndHashCode(callSuper = true)
public class WebhookError extends ServerException {

    private final String EXCEPTION = "Ошибка при отправке Вебхука";
    public WebhookError(String s) {
        super(s);
    }
}
