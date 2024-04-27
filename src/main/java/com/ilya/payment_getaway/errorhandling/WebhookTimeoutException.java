package com.ilya.payment_getaway.errorhandling;

import java.util.concurrent.TimeoutException;

public class WebhookTimeoutException extends TimeoutException {
    private final String EXCEPTION = "Ошибка при отправке Вебхука";
}
